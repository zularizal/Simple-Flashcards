package com.randomappsinc.simpleflashcards.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.FlashcardsOverviewAdapter;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.persistence.DatabaseManager;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;

public class EditFlashcardSetActivity extends StandardActivity {

    @BindView(R.id.no_flashcards) TextView noFlashcards;
    @BindView(R.id.flashcards) ListView flashcards;
    @BindView(R.id.add_flashcard) FloatingActionButton addFlashcard;
    @BindString(R.string.new_flashcard_set_name) String newSetName;

    private FlashcardsOverviewAdapter adapter;
    private int setId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_flashcard_set);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ButterKnife.bind(this);

        setId = getIntent().getIntExtra(Constants.FLASHCARD_SET_ID_KEY, 0);
        setTitle(DatabaseManager.get().getSetName(setId));

        addFlashcard.setImageDrawable(
                new IconDrawable(this, IoniconsIcons.ion_android_add)
                        .colorRes(R.color.white));
        adapter = new FlashcardsOverviewAdapter(this, setId, noFlashcards);
        flashcards.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.refreshSet();
    }

    @OnItemClick(R.id.flashcards)
    public void onFlashcardClick(int position) {
        Intent intent = new Intent(this, FlashcardFormActivity.class);
        intent.putExtra(FlashcardFormActivity.FLASHCARD_ID_KEY, adapter.getItem(position).getId());
        intent.putExtra(FlashcardFormActivity.UPDATE_MODE_KEY, true);
        startActivity(intent);
    }

    @OnClick(R.id.add_flashcard)
    public void addFlashcard() {
        Intent intent = new Intent(this, FlashcardFormActivity.class);
        intent.putExtra(Constants.FLASHCARD_SET_ID_KEY, setId);
        startActivity(intent);
    }

    public void showRenameDialog() {
        new MaterialDialog.Builder(this)
                .title(R.string.rename_flashcard_set)
                .input(newSetName, "", new MaterialDialog.InputCallback() {
                    @Override
                    public void onInput(MaterialDialog dialog, CharSequence input) {
                        boolean submitEnabled = !input.toString().trim().isEmpty();
                        dialog.getActionButton(DialogAction.POSITIVE).setEnabled(submitEnabled);
                    }
                })
                .alwaysCallInputCallback()
                .negativeText(android.R.string.no)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        if (which == DialogAction.POSITIVE) {
                            String newSetName = dialog.getInputEditText().getText().toString();
                            DatabaseManager.get().renameSet(setId, newSetName);
                            setTitle(newSetName);
                        }
                    }
                })
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.flashcard_set_menu, menu);
        menu.findItem(R.id.rename_set).setIcon(
                new IconDrawable(this, IoniconsIcons.ion_edit)
                        .colorRes(R.color.white)
                        .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.rename_set:
                showRenameDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
