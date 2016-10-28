package com.downdogapp.sequence;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.downdogapp.R;
import com.downdogapp.proto.Song;
import com.downdogapp.singleton.App;
import com.downdogapp.singleton.FileSystem;
import com.downdogapp.widget.LayoutFile;
import com.downdogapp.widget.PortraitFragment;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

@LayoutFile(R.layout.playlist_fragment)
public class PlaylistFragment extends PortraitFragment {

  public static final String TAG = PlaylistFragment.class.getSimpleName();

  @BindView(R.id.list_view) ListView listView;
  private List<Song> songs;

  public static PlaylistFragment newInstance(List<Song> songs) {
    PlaylistFragment playlistFragment = new PlaylistFragment();
    playlistFragment.songs = songs;
    return playlistFragment;
  }

  @Override
  public void onViewCreated(View view) {
    App.INSTANCE.initBlur(view, PostPracticeFragment.TAG, this, false);

    listView.setAdapter(new PlaylistAdapter());
  }

  @OnClick(R.id.back_button)
  public void backButtonClicked() {
    App.INSTANCE.popFragment();
  }

  private class PlaylistAdapter extends ArrayAdapter<Song> {

    public PlaylistAdapter() {
      super(App.INSTANCE.activity, R.layout.playlist_fragment_row, songs);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
      Song song = getItem(position);
      ViewHolder holder;
      if (view != null) {
        holder = (ViewHolder) view.getTag();
      } else {
        view = LayoutInflater.from(getContext()).inflate(R.layout.playlist_fragment_row, parent, false);
        holder = new ViewHolder(view);
        view.setTag(holder);
      }

      holder.songTitle.setText(song.title);
      holder.artist.setText(song.artist);
      FileSystem.INSTANCE.maybeMoveArtworkFromCacheDir(song.artwork);
      File file = new File(FileSystem.INSTANCE.getArtworkDir(), song.artwork);
      if (file.exists()) {
        holder.artwork.setImageDrawable(App.INSTANCE.getDrawable(file));
      } else {
        Picasso.with(getContext()).load(App.MANIFEST.artworkUrl + song.artwork).into(holder.artwork);
      }
      view.setOnClickListener(v ->
          App.INSTANCE.pushFragment(SongFragment.newInstance(song, false, PlaylistFragment.TAG)));

      return view;
    }
  }

  static class ViewHolder {
    @BindView(R.id.song_title) TextView songTitle;
    @BindView(R.id.artist) TextView artist;
    @BindView(R.id.artwork) ImageView artwork;

    public ViewHolder(View view) {
      ButterKnife.bind(this, view);
    }
  }
}
