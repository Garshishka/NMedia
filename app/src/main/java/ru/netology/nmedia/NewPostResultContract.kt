package ru.netology.nmedia

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import ru.netology.nmedia.activity.NewPostActivity

class NewOrEditPostResultContract:ActivityResultContract<String,String?>() {
    override fun createIntent(context: Context, input: String): Intent {
        val intent =  Intent(context, NewPostActivity::class.java)
        intent.putExtra("editText",input)
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): String? =
        if (resultCode == Activity.RESULT_OK) {
            intent?.getStringExtra(Intent.EXTRA_TEXT)
        } else {
            null
        }
}