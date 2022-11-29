// Generated code from Butter Knife. Do not modify!
package com.ensias.healthcareapp.fragment;

import android.view.View;
import android.widget.TextView;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import butterknife.Unbinder;
import butterknife.internal.DebouncingOnClickListener;
import butterknife.internal.Utils;
import com.ensias.healthcareapp.R;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BookingStep3Fragment_ViewBinding implements Unbinder {
  private BookingStep3Fragment target;

  private View view7f0a006e;

  @UiThread
  public BookingStep3Fragment_ViewBinding(final BookingStep3Fragment target, View source) {
    this.target = target;

    View view;
    target.txt_booking_berber_text = Utils.findRequiredViewAsType(source, R.id.txt_booking_berber_text, "field 'txt_booking_berber_text'", TextView.class);
    target.txt_booking_time_text = Utils.findRequiredViewAsType(source, R.id.txt_booking_time_text, "field 'txt_booking_time_text'", TextView.class);
    target.txt_booking_type = Utils.findRequiredViewAsType(source, R.id.txt_booking_type, "field 'txt_booking_type'", TextView.class);
    target.txt_booking_phone = Utils.findRequiredViewAsType(source, R.id.txt_booking_phone, "field 'txt_booking_phone'", TextView.class);
    view = Utils.findRequiredView(source, R.id.btn_confirm, "method 'confirmeApointement'");
    view7f0a006e = view;
    view.setOnClickListener(new DebouncingOnClickListener() {
      @Override
      public void doClick(View p0) {
        target.confirmeApointement();
      }
    });
  }

  @Override
  @CallSuper
  public void unbind() {
    BookingStep3Fragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.txt_booking_berber_text = null;
    target.txt_booking_time_text = null;
    target.txt_booking_type = null;
    target.txt_booking_phone = null;

    view7f0a006e.setOnClickListener(null);
    view7f0a006e = null;
  }
}
