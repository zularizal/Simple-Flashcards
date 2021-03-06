package com.randomappsinc.simpleflashcards.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.joanzapata.iconify.IconDrawable;
import com.joanzapata.iconify.fonts.IoniconsIcons;
import com.randomappsinc.simpleflashcards.R;
import com.randomappsinc.simpleflashcards.adapters.FlashcardSetsAdapter;
import com.randomappsinc.simpleflashcards.constants.Constants;
import com.randomappsinc.simpleflashcards.dialogs.DeleteFlashcardSetDialog;
import com.randomappsinc.simpleflashcards.dialogs.FlashcardSetCreatorDialog;
import com.randomappsinc.simpleflashcards.persistence.PreferencesManager;
import com.randomappsinc.simpleflashcards.persistence.models.FlashcardSet;
import com.randomappsinc.simpleflashcards.utils.UIUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

public class MainActivity extends StandardActivity
        implements FlashcardSetsAdapter.Listener, DeleteFlashcardSetDialog.Listener {

    @BindView(R.id.parent) View parent;
    @BindView(R.id.flashcard_set_search) EditText setSearch;
    @BindView(R.id.clear_search) View clearSearch;
    @BindView(R.id.flashcard_sets) ListView sets;
    @BindView(R.id.no_sets) TextView noSets;
    @BindView(R.id.add_flashcard_set) FloatingActionButton addFlashcardSet;

    private FlashcardSetsAdapter adapter;
    private FlashcardSetCreatorDialog flashcardSetCreatorDialog;
    private DeleteFlashcardSetDialog deleteFlashcardSetDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        PreferencesManager preferencesManager = PreferencesManager.get();
        preferencesManager.logAppOpen();
        if (preferencesManager.isFirstTimeUser()) {
            preferencesManager.rememberWelcome();
            new MaterialDialog.Builder(this)
                    .title(R.string.welcome)
                    .content(R.string.ask_for_help)
                    .positiveText(android.R.string.yes)
                    .show();
        } else if (preferencesManager.shouldAskForRating()) {
            UIUtils.askForRating(this);
        } else if (preferencesManager.shouldAskForShare()) {
            UIUtils.askToShare(this);
        }

        addFlashcardSet.setImageDrawable(new IconDrawable(this, IoniconsIcons.ion_android_add)
                .colorRes(R.color.white)
                .actionBarSize());

        flashcardSetCreatorDialog = new FlashcardSetCreatorDialog(this, setCreatedListener);
        deleteFlashcardSetDialog = new DeleteFlashcardSetDialog(this, this);

        adapter = new FlashcardSetsAdapter(this, this, noSets);
        sets.setAdapter(adapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.refreshContent(setSearch.getText().toString());
    }

    @OnTextChanged(value = R.id.flashcard_set_search, callback = OnTextChanged.Callback.AFTER_TEXT_CHANGED)
    public void afterTextChanged(Editable input) {
        adapter.refreshContent(input.toString());
        clearSearch.setVisibility(input.length() == 0 ? View.GONE : View.VISIBLE);
    }

    @OnClick(R.id.clear_search)
    public void clearSearch() {
        setSearch.setText("");
    }

    @OnClick(R.id.add_flashcard_set)
    public void addSet() {
        flashcardSetCreatorDialog.show();
    }

    private final FlashcardSetCreatorDialog.Listener setCreatedListener =
            new FlashcardSetCreatorDialog.Listener() {
                @Override
                public void onFlashcardSetCreated(int createdSetId) {
                    Intent intent = new Intent(MainActivity.this, EditFlashcardSetActivity.class);
                    intent.putExtra(Constants.FLASHCARD_SET_ID_KEY, createdSetId);
                    startActivity(intent);
                }
            };

    @Override
    public void browseFlashcardSet(FlashcardSet flashcardSet) {
        if (flashcardSet.getFlashcards().isEmpty()) {
            UIUtils.showSnackbar(
                    parent,
                    getString(R.string.no_flashcards_for_browsing),
                    Snackbar.LENGTH_LONG);
        } else {
            startActivity(new Intent(
                    this, StudyModeActivity.class)
                    .putExtra(Constants.FLASHCARD_SET_ID_KEY, flashcardSet.getId()));
        }
    }

    @Override
    public void takeQuiz(FlashcardSet flashcardSet) {
        if (flashcardSet.getFlashcards().size() < 2) {
            UIUtils.showSnackbar(parent, getString(R.string.not_enough_for_quiz), Snackbar.LENGTH_LONG);
        } else {
            startActivity(new Intent(
                    this, QuizActivity.class)
                    .putExtra(Constants.FLASHCARD_SET_ID_KEY, flashcardSet.getId()));
        }
    }

    @Override
    public void editFlashcardSet(FlashcardSet flashcardSet) {
        startActivity(new Intent(
                this, EditFlashcardSetActivity.class)
                .putExtra(Constants.FLASHCARD_SET_ID_KEY, flashcardSet.getId()));
    }

    @Override
    public void deleteFlashcardSet(FlashcardSet flashcardSet) {
        deleteFlashcardSetDialog.show(flashcardSet.getId());
    }

    @Override
    public void onFlashcardSetDeleted() {
        adapter.refreshContent(setSearch.getText().toString());
        UIUtils.showSnackbar(parent, getString(R.string.flashcard_set_deleted), Snackbar.LENGTH_LONG);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        menu.findItem(R.id.settings).setIcon(
                new IconDrawable(this, IoniconsIcons.ion_android_settings)
                        .colorRes(R.color.white)
                        .actionBarSize());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
