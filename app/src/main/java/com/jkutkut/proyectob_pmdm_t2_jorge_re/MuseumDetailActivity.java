package com.jkutkut.proyectob_pmdm_t2_jorge_re;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.jkutkut.proyectob_pmdm_t2_jorge_re.api.MuseumAPI;
import com.jkutkut.proyectob_pmdm_t2_jorge_re.api.RetrofitClient;
import com.jkutkut.proyectob_pmdm_t2_jorge_re.api.result.Museum;
import com.jkutkut.proyectob_pmdm_t2_jorge_re.api.result.MuseumResultAPI;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MuseumDetailActivity extends AppCompatActivity {

    public static final String ARG = "museum";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_detail);

        Museum museum = (Museum) getIntent().getSerializableExtra(ARG);

        String[] id_split = museum.getAtId().split("/");
        String id = id_split[id_split.length - 1];

//        loadData(museum);

        getData(
            id,
            new Callback<MuseumResultAPI>() {
                @Override
                public void onResponse(@NonNull Call<MuseumResultAPI> call, @NonNull Response<MuseumResultAPI> response) {
                    if (!response.isSuccessful()) {
                        Toast.makeText(getBaseContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    MuseumResultAPI result = response.body();
                    assert result != null;
                    loadData(result.getMuseums().get(0));
                }

                @Override
                public void onFailure(@NonNull Call<MuseumResultAPI> call, @NonNull Throwable t) {
                    Toast.makeText(getBaseContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    throw new RuntimeException(t);
                }
            }
        );
    }

    private void getData(String museum_id, Callback<MuseumResultAPI> callback) {
        Retrofit r = RetrofitClient.getClient(MuseumAPI.URL);
        MuseumAPI museumAPI = r.create(MuseumAPI.class);
        Call<MuseumResultAPI> call = museumAPI.getMuseum(museum_id);
        call.enqueue(callback);
    }

    private void loadData(Museum museum) {
        TextView txtvName = findViewById(R.id.txtvName);
        TextView txtvAddress = findViewById(R.id.txtvAddress);
        TextView txtvDescription = findViewById(R.id.txtvDescription);
        TextView txtvSchedule = findViewById(R.id.txtvSchedule);

        txtvName.setText(museum.getTitle());
        txtvAddress.setText(String.format(
                getString(R.string.address_template),
                museum.getAddress().getStreetAddress(),
                museum.getAddress().getPostalCode(),
                museum.getAddress().getLocality()
        ));
        txtvDescription.setText(
                museum.getOrganization().getOrganizationDesc()
        );
        txtvSchedule.setText(
                museum.getOrganization().getSchedule()
        );
    }
}