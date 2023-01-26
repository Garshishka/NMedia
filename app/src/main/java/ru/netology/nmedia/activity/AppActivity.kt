package ru.netology.nmedia.activity

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_INDEFINITE
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.messaging.FirebaseMessaging
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.databinding.ActivityAppBinding
import ru.netology.nmedia.di.DependencyContainer
import ru.netology.nmedia.viemodel.AuthViewModel
import ru.netology.nmedia.viemodel.ViewModelFactory

class AppActivity : AppCompatActivity() {
    private val dependencyContainer = DependencyContainer.getInstance()
    private val authViewModel: AuthViewModel by viewModels(
        factoryProducer = {
            ViewModelFactory(
                dependencyContainer.repository,
                dependencyContainer.appAuth,
                dependencyContainer.apiService
            )
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityAppBinding.inflate(layoutInflater)
        setContentView(binding.root)

        intent?.let {
            if (it.action != Intent.ACTION_SEND) {
                return@let
            }

            val text = it.getStringExtra(Intent.EXTRA_TEXT)
            if (text.isNullOrBlank()) {
                Snackbar.make(binding.root, R.string.error_empty_content, LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok) {
                        finish()
                    }
                    .show()
                return@let
            }
            val navHostFragment =
                supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
            navHostFragment.navController.navigate(
                R.id.action_feedFragment_to_newPostFragment,
                Bundle().apply { textArg = text }
            )
        }

        checkGoogleApiAvailability()

        authViewModel.state.observe(this) {
            invalidateOptionsMenu()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_auth, menu)

        menu.let {
            it.setGroupVisible(R.id.authorized, authViewModel.authorized)
            it.setGroupVisible(R.id.unauthorized, !authViewModel.authorized)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.logOut -> {
                if (findNavController(R.id.fragment_container).currentDestination?.id == R.id.newPostFragment) {
                    false
                } else {
                    dependencyContainer.appAuth.removeAuth()
                    true
                }
            }
            R.id.signIn -> {
                findNavController(R.id.fragment_container).navigate(R.id.action_sign_in)
                true
            }
            R.id.signUp -> {
                findNavController(R.id.fragment_container).navigate(R.id.action_sign_up)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun checkGoogleApiAvailability() {
        with(GoogleApiAvailability.getInstance()) {
            val code = isGooglePlayServicesAvailable(this@AppActivity)
            if (code == ConnectionResult.SUCCESS) {
                return@with
            }
            if (isUserResolvableError(code)) {
                getErrorDialog(this@AppActivity, code, 9000)?.show()
                return
            }
            Toast.makeText(this@AppActivity, R.string.google_play_unavailable, Toast.LENGTH_LONG)
                .show()
        }

        FirebaseMessaging.getInstance().token.addOnSuccessListener {
            println(it)
            //Toast.makeText(this@AppActivity,it,Toast.LENGTH_LONG).show()
        }
    }
}