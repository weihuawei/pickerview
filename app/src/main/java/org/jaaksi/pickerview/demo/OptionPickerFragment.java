package org.jaaksi.pickerview.demo;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.jaaksi.pickerview.dataset.OptionDataSet;
import org.jaaksi.pickerview.demo.model.City;
import org.jaaksi.pickerview.demo.model.County;
import org.jaaksi.pickerview.demo.model.Province;
import org.jaaksi.pickerview.picker.BasePicker;
import org.jaaksi.pickerview.picker.OptionPicker;
import org.jaaksi.pickerview.widget.DefaultCenterDecoration;
import org.jaaksi.pickerview.widget.PickerView;
import util.DataParseUtil;
import util.StreamUtil;

/**
 * 演示topbar,CenterDecoration,padding，interceptor,
 */
public class OptionPickerFragment extends BaseFragment
  implements View.OnClickListener, OptionPicker.OnOptionSelectListener,
  CompoundButton.OnCheckedChangeListener {

  private OptionPicker mPicker;
  private Button mBtnShow;
  private CheckBox cbForeign;
  private List<Province> citys;
  // 2-3-县4
  private String provinceId /*= "200"*/, cityId /*= "230"*/, countyId/* = "234"*/;

  @Override
  protected int getLayoutId() {
    return R.layout.fragment_option_picker;
  }

  @Override
  protected void initView(View view) {
    cbForeign = view.findViewById(R.id.cb_foreign);
    mBtnShow = view.findViewById(R.id.btn_show);
    mBtnShow.setOnClickListener(this);
    cbForeign.setOnCheckedChangeListener(this);
    final DefaultCenterDecoration decoration = new DefaultCenterDecoration(getActivity());
    decoration.setMargin(0, 0, 0, 0);
    //decoration.setLineWidth()
    mPicker = new OptionPicker.Builder(mActivity, 3, this)
      .setInterceptor(new BasePicker.Interceptor() {
        @Override
        public void intercept(PickerView pickerView, LinearLayout.LayoutParams params) {
          // 修改中心装饰线
          pickerView.setCenterDecoration(decoration);
        }
      })
      .create();
    // 设置标题，这里调用getTopBar来设置标题
    //DefaultTopBar topBar = (DefaultTopBar) mPicker.getTopBar();
    mPicker.getTopBar().getTitleView().setText("请选择城市");
    resetPicker();
  }

  private void resetPicker() {
    provinceId = null;
    cityId = null;
    countyId = null;
    if (cbForeign.isChecked()) {
      testForeign();
    } else {
      if (citys == null) {
        citys = DataParseUtil.toList(StreamUtil.get(getActivity(), R.raw.city), Province.class);
      }
      mPicker.setData(citys);
    }
  }

  private void testForeign() {
    List<Province> provinces = new ArrayList<>();
    List<City> cities = new ArrayList<>();
    List<County> counties = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Province province = new Province();
      province.id = i;
      province.name = "省" + i;
      provinces.add(province);

      City city = new City();
      city.id = 10 * i;
      city.name = "市" + i;
      cities.add(city);

      County county = new County();
      county.id = 100 * i;
      county.name = "县" + i;
      counties.add(county);
    }
    mPicker.setData(provinces, cities, counties);
  }

  private List<Province> createForeignData() {
    List<Province> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Province province = new Province();
      province.id = 100 * i;
      province.name = "省" + i;
      province.citys = new ArrayList<>();
      for (int j = 0; j < 10; j++) {
        City city = new City();
        city.id = 10 * j;
        city.name = "市" + j;
        city.counties = new ArrayList<>();
        for (int k = 0; k < 10; k++) {
          County county = new County();
          county.id = k;
          county.name = "县" + k;
          city.counties.add(county);
        }
        province.citys.add(city);
      }
      list.add(province);
    }
    return list;
  }

  private List<Province> createData() {
    List<Province> list = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      Province province = new Province();
      province.id = 100 * i;
      province.name = "省" + i;
      province.citys = new ArrayList<>();
      for (int j = 0; j < (i == 1 ? 1 : 10); j++) {
        City city = new City();
        city.id = province.id + 10 * j;
        city.name = i + "-市" + j;
        city.counties = new ArrayList<>();
        for (int k = 0; k < (i == 0 && j == 0 ? 0 : 10); k++) {
          County county = new County();
          county.id = city.id + k;
          county.name = i + "-" + j + "-县" + k;
          city.counties.add(county);
        }
        province.citys.add(city);
      }
      list.add(province);
    }
    return list;
  }

  @Override
  public void onClick(View v) {
    // 直接传入选中的值
    mPicker.setSelectedWithValues(provinceId, cityId, countyId);
    mPicker.show();
  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    resetPicker();
  }

  @Override
  public void onOptionSelect(OptionPicker picker, int[] selectedPosition,
    OptionDataSet[] selectedOptions) {
    System.out.println("selectedPosition = " + Arrays.toString(selectedPosition));
    String text;
    Province province = (Province) selectedOptions[0];
    provinceId = province.getValue();
    City city = (City) selectedOptions[1];
    County county = (County) selectedOptions[2];
    if (city == null) {
      cityId = null;
      countyId = null;
      text = province.name;
    } else {
      cityId = city.getValue();
      if (county == null) {
        countyId = null;
        text = city.name;
      } else {
        countyId = county.getValue();
        text = county.name;
      }
    }

    mBtnShow.setText(text);
  }
}
