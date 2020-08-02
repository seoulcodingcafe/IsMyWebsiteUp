package io.github.ismywebsiteup;

import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import io.github.ismywebsiteup.R;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

public class WelcomeActivity extends IntroActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addSlide(new SimpleSlide.Builder().title(R.string.welcome)
				.description(R.string.swipe_to_read_the_quick_guide_or_press_the_left_button_to_close_the_guide)
				.image(R.drawable.welcome).background(R.color.welcome_background_light)
				.backgroundDark(R.color.welcome_background_dark).scrollable(false).build());
		addSlide(new SimpleSlide.Builder().title(R.string.type_url_or_web_address).description("")
				.image(R.drawable.type_url).background(R.color.welcome_background_light_1)
				.backgroundDark(R.color.welcome_background_dark_1).scrollable(false).build());
		addSlide(new SimpleSlide.Builder().title(R.string.configure_more_if_you_want).description("")
				.image(R.drawable.configure_more).background(R.color.welcome_background_light_2)
				.backgroundDark(R.color.welcome_background_dark_2).scrollable(false).build());
		addSlide(new SimpleSlide.Builder().title(R.string.run_check_now_or_schedule_regular_automatic_check)
				.description("").image(R.drawable.run_or_schedule).background(R.color.welcome_background_light_3)
				.backgroundDark(R.color.welcome_background_dark_3).scrollable(false).build());
		addSlide(new SimpleSlide.Builder().title(R.string.for_schedule_type_name_of_schedule_and_time).description("")
				.image(R.drawable.type_time_of_schedule).background(R.color.welcome_background_light_4)
				.backgroundDark(R.color.welcome_background_dark_4).scrollable(false).build());
		addSlide(new SimpleSlide.Builder().title(R.string.see_results).description("").image(R.drawable.see_results)
				.background(R.color.welcome_background_light_5).backgroundDark(R.color.welcome_background_dark_5)
				.scrollable(false).build());
		addSlide(new SimpleSlide.Builder()
				.title(R.string.for_schedule_you_will_also_notification_if_your_website_is_down).description("")
				.image(R.drawable.you_will_also_get_notification).background(R.color.welcome_background_light_6)
				.backgroundDark(R.color.welcome_background_dark_6).scrollable(false).build());
		addSlide(new SimpleSlide.Builder().title(R.string.discover_many_extra_features).description("")
				.image(R.drawable.discover_many_extra_features).background(R.color.welcome_background_light_7)
				.backgroundDark(R.color.welcome_background_dark_7).scrollable(false).build());
		addSlide(new SimpleSlide.Builder().title(R.string.you_are_ready_now)
				.description(R.string.thank_you_for_choosing_free_and_open_source_software)
				.image(R.drawable.you_are_ready_now).background(R.color.welcome_background_light_8)
				.backgroundDark(R.color.welcome_background_dark_8).scrollable(false).build());
		setButtonBackVisible(true);
		setButtonBackFunction(BUTTON_BACK_FUNCTION_SKIP);
		setButtonNextVisible(true);
		setButtonNextFunction(BUTTON_NEXT_FUNCTION_NEXT_FINISH);

		addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}

			@Override
			public void onPageSelected(int position) {
				if (position > 0)
					setButtonBackFunction(BUTTON_BACK_FUNCTION_BACK);
				else
					setButtonBackFunction(BUTTON_BACK_FUNCTION_SKIP);
			}
		});
	}

}
