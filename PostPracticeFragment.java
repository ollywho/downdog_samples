package com.downdogapp.sequence;

import android.net.Uri;
import android.view.View;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.OnClick;
import com.downdogapp.R;
import com.downdogapp.StartFragment;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.downdogapp.MembershipFragment;
import com.downdogapp.MessageFragment;
import com.downdogapp.proto.PostPracticeConfig;
import com.downdogapp.proto.Request;
import com.downdogapp.singleton.App;
import com.downdogapp.singleton.Network;
import com.downdogapp.widget.LayoutFile;
import com.downdogapp.widget.PortraitFragment;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import java.util.List;

@LayoutFile(R.layout.post_practice_fragment)
public class PostPracticeFragment extends PortraitFragment {

  public static final String TAG = PostPracticeFragment.class.getSimpleName();

  public static PostPracticeFragment newInstance(SequenceModel model) {
    PostPracticeFragment postPracticeFragment = new PostPracticeFragment();
    postPracticeFragment.model = model;
    return postPracticeFragment;
  }

  private SequenceModel model;
  @BindViews({R.id.star_0, R.id.star_1, R.id.star_2, R.id.star_3, R.id.star_4})
  List<ImageView> stars;
  @BindView(R.id.continue_button) TextView continueButton;
  @BindView(R.id.playlist_button) TextView playlistButton;
  @BindView(R.id.title_label) TextView titleLabel;
  @BindView(R.id.content_label) TextView contentLabel;
  @BindView(R.id.feedback_button) TextView feedbackButton;
  @BindView(R.id.membership_button) TextView membershipButton;
  @BindView(R.id.facebook_button) TextView facebookButton;
  @BindView(R.id.rating_container) View ratingContainer;

  @Override
  public void onViewCreated(View view) {
    App.INSTANCE.initBlur(view, SequenceFragment.TAG, this, false);

    PostPracticeConfig config = model.sequence.postPracticeConfig;

    if (config.title != null) {
      titleLabel.setText(config.title.text);
      if (config.title.fontSize > 0) {
        titleLabel.setTextSize((float) config.title.fontSize);
      }
    } else {
      titleLabel.setVisibility(View.INVISIBLE);
    }
    if (config.content != null) {
      contentLabel.setText(config.content.text);
      if (config.content.fontSize > 0) {
        contentLabel.setTextSize((float) config.content.fontSize);
      }
    } else {
      contentLabel.setVisibility(View.INVISIBLE);
    }
    ratingContainer.setVisibility(config.displayRating ? View.VISIBLE : View.INVISIBLE);
    if (config.feedbackMessage == null) {
      feedbackButton.setVisibility(View.GONE);
      LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) playlistButton.getLayoutParams();
      params.setMargins(0, 0, 0, 0);
    }
    if (model.getSongsPlayed().isEmpty()) {
      playlistButton.setVisibility(View.GONE);
    }
    if (config.shareToFacebookUrl == null) {
      facebookButton.setVisibility(View.GONE);
    }
    if (!config.displayBecomeAMember) {
      membershipButton.setVisibility(View.GONE);
    }
  }

  @OnClick({R.id.star_0, R.id.star_1, R.id.star_2, R.id.star_3, R.id.star_4})
  public void starClicked(ImageView star) {
    int rating = stars.indexOf(star) + 1;
    for (int i = 0; i < stars.size(); i++) {
      if (i < rating) {
        stars.get(i).setImageResource(R.drawable.star_filled);
      } else {
        stars.get(i).setImageResource(R.drawable.star);
      }
    }
    Network.INSTANCE.post(new Request.RecordRatingRequest(model.sequence.practiceId, rating), null);
  }

  @OnClick(R.id.feedback_button)
  public void feedbackClicked() {
    App.INSTANCE.pushFragment(MessageFragment.newInstance(model.sequence.postPracticeConfig.feedbackMessage,
        model.sequence.practiceId, model.getTime(), PostPracticeFragment.TAG));
  }

  @OnClick(R.id.playlist_button)
  public void playlistClicked() {
    App.INSTANCE.pushFragment(PlaylistFragment.newInstance(model.getSongsPlayed()));
  }

  @OnClick(R.id.facebook_button)
  public void facebookClicked() {
    ShareLinkContent content = new ShareLinkContent.Builder()
        .setContentUrl(Uri.parse(model.sequence.postPracticeConfig.shareToFacebookUrl))
        .build();
    ShareDialog.show(this, content);
  }

  @OnClick(R.id.membership_button)
  public void membershipClicked() {
    App.INSTANCE.pushFragment(MembershipFragment.newInstance(PostPracticeFragment.TAG, null, 0));
  }

  @OnClick(R.id.continue_button)
  public void continueClicked() {
    App.INSTANCE.popToFragment(StartFragment.TAG);
  }
}
