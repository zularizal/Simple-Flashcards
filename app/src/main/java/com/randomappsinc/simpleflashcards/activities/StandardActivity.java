package com.randomappsinc.simpleflashcards.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

public class StandardActivity extends AppCompatActivity {

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        UIUtils.closeKeyboard(this);
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.slide_left_out, R.anim.slide_left_in);
    }

    @Override
    public void finish() {
        UIUtils.closeKeyboard(this);
        super.finish();
        overridePendingTransition(R.anim.slide_right_out, R.anim.slide_right_in);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
