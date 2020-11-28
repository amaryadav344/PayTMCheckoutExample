package com.loopwiki.paytmcheckoutexample.Activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.loopwiki.paytmcheckoutexample.API.APIClient;
import com.loopwiki.paytmcheckoutexample.API.APIInterface;
import com.loopwiki.paytmcheckoutexample.Fragment.CartFragment;
import com.loopwiki.paytmcheckoutexample.Model.ChecksumResponse;
import com.loopwiki.paytmcheckoutexample.Model.Product;
import com.loopwiki.paytmcheckoutexample.Fragment.ProductsFragment;
import com.loopwiki.paytmcheckoutexample.R;
import com.loopwiki.paytmcheckoutexample.Model.User;
import com.loopwiki.paytmcheckoutexample.Model.UserDetails;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PaymentActivity extends AppCompatActivity implements ProductsFragment.ProductInteractionListener, CartFragment.CartInteractionListener {
    public static final String TAG = PaymentActivity.class.getSimpleName();
    FragmentManager fragmentManager;
    ProductsFragment productsFragment;
    int cartCount = 0;
    @BindView(R.id.textViewCartCount)
    TextView textViewCartCount;
    @BindView(R.id.imageViewCart)
    ImageView imageViewCart;
    List<Product> products;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        ButterKnife.bind(this);
        fragmentManager = getSupportFragmentManager();
        productsFragment = ProductsFragment.newInstance();
        products = getProducts();
        productsFragment.products = products;
        fragmentManager.beginTransaction().replace(R.id.main_content, productsFragment).commit();
        imageViewCart.setOnClickListener(v -> {
            CartFragment cartFragment = new CartFragment();
            List<Product> productList = new ArrayList<>();
            for (Product product : products) {
                if (product.isAddedToCart()) {
                    productList.add(product);
                }
            }
            cartFragment.products = productList;
            fragmentManager.beginTransaction().replace(R.id.main_content, cartFragment).addToBackStack(ProductsFragment.TAG).commit();
        });

    }

    // Callback from Products fragment when product is added
    @Override
    public void ProductAddedToCart(Product product) {
        cartCount++;
        textViewCartCount.setVisibility(View.VISIBLE);
        textViewCartCount.setText(String.valueOf(cartCount));
        Toast.makeText(this, getString(R.string.product_added), Toast.LENGTH_SHORT).show();
    }

    // Callback from Products fragment when product is removed
    @Override
    public void ProductRemovedFromCart(Product product) {
        cartCount--;
        textViewCartCount.setText(String.valueOf(cartCount));
        if (cartCount == 0) {
            textViewCartCount.setVisibility(View.GONE);
        }
        Toast.makeText(this, getString(R.string.product_removed), Toast.LENGTH_SHORT).show();
    }

    // method to create dummy product
    private List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        int[] ImageUrl = {R.drawable.one, R.drawable.two, R.drawable.three, R.drawable.four, R.drawable.five, R.drawable.six};
        String[] Title = {"HRX by Hrithik", "Crew STREET", "Royal Enfield", "Kook N Keech", "ADIDAS", "UNDER ARMOUR"};
        int[] Price = {5000, 2000, 1500, 3000, 1256, 700};
        boolean[] IsNew = {true, false, false, true, true, false};
        for (int i = 0; i < ImageUrl.length; i++) {
            Product product = new Product();
            product.setName(Title[i]);
            product.setImageResourceId(ImageUrl[i]);
            product.setNew(IsNew[i]);
            product.setPrice(Price[i]);
            products.add(product);
        }
        return products;

    }

    // Back button press method
    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            fragmentManager.popBackStackImmediate();
        }

    }

    // Method called when product is removed from cart
    @Override
    public void RemoveProduct(Product product) {
        int index = this.products.indexOf(product);
        Product ProductToRemove = this.products.get(index);
        ProductToRemove.setAddedToCart(false);
        ProductRemovedFromCart(product);
    }

    // Method is called when we click on Pay button
    @Override
    public void ProceedToPay(int TotalPrice) {
        //launchPayUMoneyFlow(TotalPrice);
        launchPayTMCheckout(TotalPrice);
    }

    // Launch Paytm payment flow
    private void launchPayTMCheckout(int totalPrice) {
        PaytmPGService Service = PaytmPGService.getStagingService("");
        User user = UserDetails.getUser();
        String OrderId = "order" + System.currentTimeMillis();
        // Create map of input parameters
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("MID", getResources().getString(R.string.Merchant_ID));
        paramMap.put("ORDER_ID", OrderId);
        paramMap.put("CUST_ID", user.getUsername());
        paramMap.put("EMAIL", user.getUsername());
        paramMap.put("TXN_AMOUNT", totalPrice + ".00");
        paramMap.put("CHANNEL_ID", "WAP");
        paramMap.put("WEBSITE", "WEBSTAGING");
        // This is the staging value. Production value is available in your dashboard
        paramMap.put("INDUSTRY_TYPE_ID", "Retail");
        // This is the staging value. Production value is available in your dashboard
        paramMap.put("CALLBACK_URL", "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID=" + OrderId);

        APIInterface apiInterface = APIClient.getApiInterface();
        // for better security we have to create hash from each of the input parameter.
        // while transferring data this avoid data manipulation
        Call<ChecksumResponse> call = apiInterface.getCheckSum(paramMap);
        call.enqueue(new Callback<ChecksumResponse>() {
            @Override
            public void onResponse(Call<ChecksumResponse> call, Response<ChecksumResponse> response) {
                paramMap.put("CHECKSUMHASH", response.body().getChecksum());
                try {

                    PaytmOrder Order = new PaytmOrder(paramMap);

                    Service.initialize(Order, null);

                    Service.startPaymentTransaction(PaymentActivity.this, true, true, new PaytmPaymentTransactionCallback() {
                        @Override
                        public void onTransactionResponse(Bundle inResponse) {
                            // Paytm payment response
                            Toast.makeText(getApplicationContext(), "Payment Transaction response " + inResponse.toString(), Toast.LENGTH_LONG).show();
                            // Reset the cart
                            clearCart();
                            cartCount = 0;
                            fragmentManager.beginTransaction().replace(R.id.main_content, productsFragment).commit();
                            textViewCartCount.setVisibility(View.GONE);
                        }

                        @Override
                        public void networkNotAvailable() {
                        }

                        @Override
                        public void clientAuthenticationFailed(String inErrorMessage) {
                        }

                        @Override
                        public void someUIErrorOccurred(String inErrorMessage) {
                        }

                        @Override
                        public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
                        }

                        @Override
                        public void onBackPressedCancelTransaction() {
                        }

                        @Override
                        public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
                        }

                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<ChecksumResponse> call, Throwable t) {
                Log.d("error is", t.getStackTrace().toString());
            }
        });
    }

    // method to clear cart
    public void clearCart() {
        for (Product product : products) {
            if (product.isAddedToCart()) {
                product.setAddedToCart(false);
            }
        }
    }
}
