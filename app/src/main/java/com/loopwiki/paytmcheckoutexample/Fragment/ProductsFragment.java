package com.loopwiki.paytmcheckoutexample.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.loopwiki.paytmcheckoutexample.Adapater.ProductAdapter;
import com.loopwiki.paytmcheckoutexample.Util.GridSpacingItemDecoration;
import com.loopwiki.paytmcheckoutexample.Model.Product;
import com.loopwiki.paytmcheckoutexample.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ProductsFragment extends Fragment implements ProductAdapter.ProductAdapterCallBack {
   public static final String TAG = ProductsFragment.class.getSimpleName();
    private ProductInteractionListener mListener;

    public List<Product> products;

    @BindView(R.id.recyclerViewProducts)
    RecyclerView recyclerViewProducts;

    public ProductsFragment() {
        // Required empty public constructor
    }

    public static ProductsFragment newInstance() {
        ProductsFragment fragment = new ProductsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_products, container, false);
        ButterKnife.bind(this, view);
        ProductAdapter productAdapter = new ProductAdapter();
        productAdapter.setProductAdapterCallBack(this);
        productAdapter.setProducts(products);
        recyclerViewProducts.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerViewProducts.setAdapter(productAdapter);
        recyclerViewProducts.addItemDecoration(new GridSpacingItemDecoration(2, 20, true, 0));
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ProductInteractionListener) {
            mListener = (ProductInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ProductInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void ProductAddedToCart(Product product) {
        this.mListener.ProductAddedToCart(product);
    }

    @Override
    public void ProductRemovedFromCart(Product product) {
        this.mListener.ProductRemovedFromCart(product);
    }

    public interface ProductInteractionListener {
        void ProductAddedToCart(Product product);

        void ProductRemovedFromCart(Product product);
    }
}
