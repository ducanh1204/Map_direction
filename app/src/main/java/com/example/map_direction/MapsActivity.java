package com.example.map_direction;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.map_direction.Network.RetrofitService;
import com.example.map_direction.model.Example;
import com.example.map_direction.model.Polyline;
import com.example.map_direction.morder.ClassShowInformation;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private PolylineOptions polylineOptions;
    private ArrayList<ClassShowInformation> showInformationArrayList;
    private List<LatLng> latLngs;
    private final String api_key = "AIzaSyCmxFS2arHibTbROQAfTkZAJRkEpz8LErU";
    private int position_location = 0;
    private  List<Colors> colorsList = new ArrayList<>();
    private int colorIndex = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        addDataSlideInformation();
        addColor();

        getRetrofit(position_location, position_location + 1);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(21.029585, 105.836174);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 15));

        if (polylineOptions != null) {
            mMap.addPolyline(polylineOptions);
        }

        addMarkerAll();


    }

    private void getRetrofit(int location1, int location2) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);

        retrofitService.getHttp(getLatLng(location1), getLatLng(location2), api_key).enqueue(new Callback<Example>() {
            @Override
            public void onResponse(Call<Example> call, Response<Example> response) {

                polylineOptions = new PolylineOptions();
                if(colorIndex<colorsList.size()){
                    polylineOptions.color(colorsList.get(colorIndex).getColor());
                    colorIndex++;
                }else {
                    colorIndex = 0;
                    polylineOptions.color(colorsList.get(colorIndex).getColor());
                }
                polylineOptions.width(10);
                polylineOptions.addAll(decodePolyLine(response.body().getRoutes().get(0).getOverviewPolyline().getPoints()));
                SupportMapFragment supportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                assert supportMapFragment != null;
                supportMapFragment.getMapAsync((OnMapReadyCallback) MapsActivity.this);

                if (position_location < showInformationArrayList.size()-2) {
                    position_location++;
                    getRetrofit(position_location, position_location + 1);
                } else if (position_location == showInformationArrayList.size()-2) {
                    position_location++;
                    getRetrofit(position_location, 0);

                }
            }

            @Override
            public void onFailure(Call<Example> call, Throwable t) {
                Toast.makeText(MapsActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
    private void addColor(){
        colorsList.add(new Colors(Color.RED));
        colorsList.add(new Colors(Color.GREEN));
        colorsList.add(new Colors(Color.BLUE));
        colorsList.add(new Colors(Color.YELLOW));
        colorsList.add(new Colors(Color.CYAN));
        colorsList.add(new Colors(Color.MAGENTA));
    }


    private void addDataSlideInformation() {
        showInformationArrayList = new ArrayList<>();

        showInformationArrayList.add(new ClassShowInformation(21.037000, 105.834727, "Lăng Bác", "Lăng Bác là nơi lưu giữ thi hài của vị lãnh tụ kính yêu. Bên ngoài lăng là những hàng tre xanh bát ngát. Lăng chủ tích mở cửa vào sáng thứ 3,4,5,7 và chủ nhật. Khi vào viếng lăng Bác, bạn chú ý ăn mặc chỉnh tề, không đem theo các thiết bị điện tử ghi hành và giữ trật tự trong lăng.", 0, "https://www.bqllang.gov.vn/images/NAM_2019/THANG_1/31-1/22.jpg"));
        showInformationArrayList.add(new ClassShowInformation(21.032555, 105.839804, "Cột cờ Hà Nội", "Kỳ đài Hà Nội hay còn được nhiều biết tới hơn với tên gọi Cột cờ Hà Nội nằm trong khuôn viên của bảo tàng lịch sử quân sự Việt Nam. Được đánh giá là công trình nguyên vẹn và hoành tráng nhất trong quần thể di tích Hoàng thành Thăng Long, Cột Cờ chính là điểm tham quan du lịch ở Hà Nội mà du khách không thể bỏ qua trong hành trình khám phá lịch sử của đất Hà Thành.", 1, "https://upload.wikimedia.org/wikipedia/commons/thumb/b/b1/Flag_tower%2C_Hanoi.jpg/250px-Flag_tower%2C_Hanoi.jpg"));
        showInformationArrayList.add(new ClassShowInformation(21.029565, 105.836206, "Văn Miếu - Quốc Tử Giám", "Nếu kể tên các địa điểm du lịch Hà Nội bậc nhất xưa và nay có lẽ ai cũng sẽ nghĩ ngay đến Văn Miếu Quốc Tử Giám. Đây là một quần thể kiến trúc văn hoá hàng đầu và là niềm tự hào của người dân Thủ đô khi nhắc đến truyền thống ngàn năm văn hiến của Thăng Long – Đông Đô – Hà Nội.", 2, "https://laodongthudo.vn/stores/news_dataimages/ngocthang/012020/30/13/2337_b1a29f49-f486-45b3-ae99-f8d661ff8cb6.jpg"));
        showInformationArrayList.add(new ClassShowInformation(21.025445, 105.846422, "Di Tích Lịch Sử Nhà Tù Hỏa Lò", "Nhà tù Hỏa Lò được thực dân Pháp xây dựng từ năm 1896 với tên gọi “Maison Central”, là nơi giam giữ những chiến sĩ cách mạng chống lại chế độ thực dân. Đây là một trong những công trình kiên cố vào loại bậc nhất Đông Dương khi đó. Sau ngày giải phóng thủ đô, nhà tù được đặt dưới quyền của chính quyền cách mạng. Từ năm 1963 đến 1975, nơi đây còn được sử dụng để làm nơi giam giữ những phi công Mỹ bị quân đội Việt Nam bắn rơi trong cuộc chiến tranh phá hoại miền Bắc. Nhà tù Hỏa Lò được thực dân Pháp xây dựng từ năm 1896 với tên gọi “Maison Central”, là nơi giam giữ những chiến sĩ cách mạng chống lại chế độ thực dân. Đây là một trong những công trình kiên cố vào loại bậc nhất Đông Dương khi đó. Sau ngày giải phóng thủ đô, nhà tù được đặt dưới quyền của chính quyền cách mạng. Từ năm 1963 đến 1975, nơi đây còn được sử dụng để làm nơi giam giữ những phi công Mỹ bị quân đội Việt Nam bắn rơi trong cuộc chiến tranh phá hoại miền Bắc.", 3, "https://sodulich.hanoi.gov.vn/storage/nhatuhoalo120190915230108.png"));
        showInformationArrayList.add(new ClassShowInformation(21.024118, 105.857947, "Nhà hát Lớn Hà Nội", "Nằm ở số 1 Tràng Tiền, Nhà hát lớn là một trong các địa điểm du lịch đẹp ở Hà Nội mang nhiều dấu ấn lịch sử. Đây là địa điểm tổ chức những chương trình nghệ thuật lớn của nhiều ca sĩ, nghệ sĩ tên tuổi hàng đầu Việt Nam. Du khách có thể chiêm ngưỡng kiến trúc tuyệt vời của Nhà hát Lớn hay mua vé vào xem một trong những chương trình biểu diễn thường xuyên được tổ chức để có thể tận mắt thấy được hết nội thất tráng lệ của nhà hát.", 4, "https://icdn.dantri.com.vn/zoom/1200_630/2017/nha-hat-lon-ha-noi-1499853914500-crop-1499854079654.jpg"));
        showInformationArrayList.add(new ClassShowInformation(21.028683, 105.848812, "Nhà thờ Lớn Hà Nội", "Nằm ở 40 phố Nhà Chung, phường Hàng Trống, Nhà thờ lớn là một trong những điểm đến thú vị ở Hà Nội, nơi lui tới không chỉ của các tín đồ theo đạo mà còn là địa điểm quen thuộc của giới trẻ, khách du lịch tứ phương. Nhà thờ được thiết kế theo phong cách kiến trúc Gothic trung cổ châu Âu với bức tường xây cao, có mái vòm và nhiều cửa sổ.", 5, "https://upload.wikimedia.org/wikipedia/commons/thumb/e/ea/Hanoi_sjc.jpg/1200px-Hanoi_sjc.jpg"));
        showInformationArrayList.add(new ClassShowInformation(21.028805, 105.852150, "Hồ Hoàn Kiếm", "Hồ Gươm hay hồ Hoàn Kiếm là một trong những nơi nên đến ở Hà Nội khi du lịch thủ đô. Nằm ở giữa trung tâm, Hồ Gươm được ví như trái tim của thành phố ngàn năm tuổi này.. Mặt hồ như tấm gương lớn soi bóng những cây cổ thụ, những rặng liễu thướt tha tóc rủ, những mái đền, chùa cổ kính, tháp cũ rêu phong, các toà nhà mới cao tầng vươn lên trời xanh.", 6, "https://e.dowload.vn/data/image/2020/01/08/thuyet-minh-ve-ho-guom-1.jpg"));
        showInformationArrayList.add(new ClassShowInformation(21.034399, 105.840115, "Hoàng Thành Thăng Long", "Hoàng thành Thăng Long là quần thể di tích gắn liền với sự phát triển của Thăng Long – Hà Nội, được các triều vua xây dựng trong nhiều giai đoạn lịch sử. Đây cũng là di tích quan trọng bậc nhất trong hệ thống các di tích lịch sử của Việt Nam. Đến Hoàng thành Thăng Long du khách có thể tham quan những địa điểm nổi bật như khu khảo cổ học số 18 Hoàng Diệu, Đoan Môn, Điện Kính Thiên, Bắc Môn (thành Cửa Bắc)…", 7, "https://useful.vn/wp-content/uploads/2020/04/1568099002089_4146890.png"));
    }

    private List<LatLng> decodePolyLine(final String poly) {
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len) {
            int b;
            int shift = 0;
            int result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d, lng / 100000d
            ));
        }

        return decoded;
    }
    private String getLatLng(int i) {

        String latLng = showInformationArrayList.get(i).getLatitude() + "," + showInformationArrayList.get(i).getLongitude();

        return latLng;
    }

    private void addMarkerAll() {
        latLngs = new ArrayList<>();
        for (int i = 0; i < showInformationArrayList.size(); i++) {
            final LatLng position = new LatLng(showInformationArrayList.get(i).getLatitude(), showInformationArrayList.get(i).getLongitude());
            MarkerOptions option = new MarkerOptions();
            option.position(position);
            option.title(showInformationArrayList.get(i).getTitle());
            option.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            final Marker maker = mMap.addMarker(option);
            maker.showInfoWindow();
            latLngs.add(position);
        }
    }
}