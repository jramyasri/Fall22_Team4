// Generated code from Butter Knife. Do not modify!
package com.ensias.healthcareapp.fragment;

import android.view.View;
import androidx.annotation.CallSuper;
import androidx.annotation.UiThread;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import com.ensias.healthcareapp.R;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import java.lang.IllegalStateException;
import java.lang.Override;

public class BookingStep2Fragment_ViewBinding implements Unbinder {
  private BookingStep2Fragment target;

  @UiThread
  public BookingStep2Fragment_ViewBinding(BookingStep2Fragment target, View source) {
    this.target = target;

    target.recycler_time_slot = Utils.findRequiredViewAsType(source, R.id.recycle_time_slot, "field 'recycler_time_slot'", RecyclerView.class);
    target.calendarView = Utils.findRequiredViewAsType(source, R.id.calendarView, "field 'calendarView'", HorizontalCalendarView.class);
  }

  @Override
  @CallSuper
  public void unbind() {
    BookingStep2Fragment target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");
    this.target = null;

    target.recycler_time_slot = null;
    target.calendarView = null;
  }
}
