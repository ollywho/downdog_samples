package com.downdogapp.sequence;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.OnClick;
import com.downdogapp.R;
import com.downdogapp.proto.Song;
import com.downdogapp.singleton.App;
import com.downdogapp.singleton.FileSystem;
import com.downdogapp.singleton.Logger;
import com.downdogapp.widget.BaseFragment;
import com.downdogapp.widget.LayoutFile;
import com.downdogapp.widget.SafeMediaPlayer;
import com.google.common.collect.ImmutableList;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;

@LayoutFile(R.layout.song_fragment)
public class SongFragment extends BaseFragment {

  private Song song;
  private boolean duringSequence;
  private String parentFragmentTag;
  private boolean isPlaying = false;
  private SafeMediaPlayer player = new SafeMediaPlayer();
  private volatile boolean prepared = false;
  @BindView(R.id.preview_button) ImageView previewButton;
  @BindView(R.id.play_store_view) View playStoreView;
  @BindView(R.id.itunes_view) View itunesView;
  @BindView(R.id.spotify_view) View spotifyView;
  @BindView(R.id.amazon_view) View amazonView;
  @BindView(R.id.song_title) TextView songTitle;
  @BindView(R.id.artist) TextView artist;
  @BindView(R.id.artwork) ImageView artwork;

  public static SongFragment newInstance(Song song, boolean duringSequence, String parentFragmentTag) {
    SongFragment fragment = new SongFragment();
    fragment.song = song;
    fragment.duringSequence = duringSequence;
    fragment.parentFragmentTag = parentFragmentTag;
    return fragment;
  }

  @Override
  public void onViewCreated(View view) {
    App.INSTANCE.initBlur(view, parentFragmentTag, this, false);

    songTitle.setText(song.title);
    artist.setText(song.artist);
    FileSystem.INSTANCE.maybeMoveArtworkFromCacheDir(song.artwork);
    File file = new File(FileSystem.INSTANCE.getArtworkDir(), song.artwork);
    if (file.exists()) {
      artwork.setImageDrawable(App.INSTANCE.getDrawable(file));
    } else {
      Picasso.with(App.INSTANCE.activity).load(App.MANIFEST.artworkUrl + song.artwork).into(artwork);
    }

    if (song.playStoreUrl == null) {
      playStoreView.setVisibility(View.GONE);
    }
    if (song.itunesUrl == null) {
      itunesView.setVisibility(View.GONE);
    }
    if (song.spotifyUrl == null) {
      spotifyView.setVisibility(View.GONE);
    }
    if (song.amazonUrl == null) {
      amazonView.setVisibility(View.GONE);
    }
    if (song.previewUrl != null && !duringSequence) {
      player.setOnCompletionListener(mp -> {
        isPlaying = false;
        updatePreviewImageButton();
      });
      App.INSTANCE.runInBackground(() -> {
        try {
          player.setDataSource(song.previewUrl);
          player.prepare();
          prepared = true;
        } catch (IOException e) {
          Logger.INSTANCE.logError("Error setting preview URL: " + e.getLocalizedMessage());
        }
      });
    } else {
      previewButton.setVisibility(View.INVISIBLE);
    }
  }

  @OnClick({R.id.play_store_view, R.id.itunes_view, R.id.spotify_view, R.id.amazon_view})
  public void linkClicked(View view) {
    App.INSTANCE.openFirstValidUrl(ImmutableList.of(getUrl(view.getId())));
  }

  private String getUrl(int id) {
    switch (id) {
      case R.id.play_store_view:
        return song.playStoreUrl;
      case R.id.itunes_view:
        return song.itunesUrl;
      case R.id.spotify_view:
        return song.spotifyUrl;
      case R.id.amazon_view:
        return song.amazonUrl;
      default:
        throw new IllegalStateException("Unexpected id: " + id);
    }
  }

  @OnClick(R.id.preview_button)
  public void previewButtonClicked() {
    isPlaying = !isPlaying;
    if (isPlaying) {
      maybeStartPlaying();
    } else {
      maybePause();
    }
    updatePreviewImageButton();
  }

  private void updatePreviewImageButton() {
    previewButton.setImageResource(isPlaying ? R.drawable.preview_pause_button : R.drawable.preview_play_button);
  }

  private void maybeStartPlaying() {
    if (isPlaying && !player.isPlaying()) {
      if (prepared) {
        player.start();
      } else {
        App.INSTANCE.runInMainAfter(500, () -> maybeStartPlaying());
      }
    }
  }

  private void maybePause() {
    if (player.isPlaying()) {
      player.pause();
    }
  }

  @Override
  @OnClick(R.id.cancel_button)
  public void onBackPressed() {
    maybePause();
    player.release();
    App.INSTANCE.popFragment();
  }
}
