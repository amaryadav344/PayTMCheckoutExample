package com.loopwiki.paytmcheckoutexample.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.loopwiki.paytmcheckoutexample.API.APIClient;
import com.loopwiki.paytmcheckoutexample.API.APIInterface;
import com.loopwiki.paytmcheckoutexample.Fragment.LoginFragment;
import com.loopwiki.paytmcheckoutexample.R;
import com.loopwiki.paytmcheckoutexample.Fragment.RegisterFragment;
import com.loopwiki.paytmcheckoutexample.Model.User;
import com.loopwiki.paytmcheckoutexample.Model.UserDetails;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener, RegisterFragment.OnFragmentInteractionListener {
    FragmentManager fragmentManager;
    @BindView(R.id.frameLayoutLoading)
    FrameLayout frameLayoutLoading;
    @BindView(R.id.textViewLoadingText)
    TextView textViewLoadingText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main_content, new LoginFragment()).commit();
    }


    @Override
    public void onLogin(String email, String password) {
        textViewLoadingText.setText("Logging in");
        frameLayoutLoading.setVisibility(View.VISIBLE);
        APIClient.getClient(email, password);
        APIInterface apiInterface = APIClient.getApiInterface();
        Call<User> call = apiInterface.loginUser();
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                UserDetails.setUser(response.body());
                frameLayoutLoading.setVisibility(View.GONE);
                Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }

    @Override
    public void onOpenRegister() {
        fragmentManager.beginTransaction().replace(R.id.main_content, new RegisterFragment()).addToBackStack(null).commit();
    }

    @Override
    public void onRegister(String email, String password, String fullName) {
        textViewLoadingText.setText("Creating Account");
        frameLayoutLoading.setVisibility(View.VISIBLE);
        APIInterface apiInterface = APIClient.getFreshClient().create(APIInterface.class);
        Call<User> call = apiInterface.registerUser(new User(email, "USER", fullName, password));
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                onLogin(email, password);
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

            }
        });
    }
}
