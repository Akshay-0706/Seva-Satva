package com.official.sevasatva;

import static android.content.Context.MODE_PRIVATE;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link studentNews#newInstance} factory method to
 * create an instance of this fragment.
 */


public class studentNews extends Fragment {
    // API key: c2c368b741844c39a08a194825a365e0
    private RecyclerView recyclerView;

    private ArrayList<studentNewsModel> newsList;

    private studentNewsAdapter studentNewsAdapter;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public studentNews() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment news.
     */
    // TODO: Rename and change types and number of parameters
    public static studentNews newInstance(String param1, String param2) {
        studentNews fragment = new studentNews();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_news, container, false);

        recyclerView = view.findViewById(R.id.studentNewsRecyclerView);

        newsList = new ArrayList<>();

        studentNewsAdapter = new studentNewsAdapter(newsList, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(studentNewsAdapter);

        fetchNews();

        studentNewsAdapter.notifyDataSetChanged();

        return view;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final boolean[] gone = {true};
        ConstraintLayout scrollLayout = getView().findViewById(R.id.studentNewsScrollLayout);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == 0 && !gone[0]) {
                    gone[0] = true;
                    scrollLayout.setVisibility(View.GONE);
                } else scrollLayout.setVisibility(View.VISIBLE);
                Log.i("state", "onScrollStateChanged: " + newState);

            }
        });

        scrollLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerView.scrollToPosition(0);
                scrollLayout.setVisibility(View.GONE);
                gone[0] = false;
            }
        });
    }

    private void fetchNews() {

        Dialog loadingDialog = new Dialog(getContext());
        loadingDialog.setContentView(R.layout.fragment_loading);
        loadingDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        String courseName = getContext().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE).getString("cn", "");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("news").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                DataSnapshot dataSnapshot = snapshot.child(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp"));
                newsList.clear(); // clears and add specfic categories
                String category = "All";
                if (snapshot.hasChild(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp")))
                    category = (String) snapshot.child(getContext().getSharedPreferences("PREFERENCE", MODE_PRIVATE).getString("cc", "temp")).getValue();

                String url = "https://newsapi.org/v2/everything?language=en&sortBy=publishedAt&q=" + category + "&from=2022-04-03" + "&apiKey=c2c368b741844c39a08a194825a365e0";

                String mainUrl = "https://newsapi.org/";

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(mainUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                studentNewsRetrofit studentNewsRetrofit = retrofit.create(studentNewsRetrofit.class);
                Call<studentNewsFetcherModel> call;

                if (category.equals("All")) {
                    call = studentNewsRetrofit.getAllNews(url);
                } else {
                    call = studentNewsRetrofit.getNewsByCategory(url);
                }

                call.enqueue(new Callback<studentNewsFetcherModel>() {
                    @Override
                    public void onResponse(Call<studentNewsFetcherModel> call, Response<studentNewsFetcherModel> response) {
                        Log.i("Response", "onResponse: " + response);

                        ArrayList<studentNewsModel> news = response.body().getArticles();

                        for (int i = 0; i < news.size(); i++) {
                            studentNewsModel studentNewsModel = new studentNewsModel(news.get(i).getTitle(), news.get(i).getDescription(), news.get(i).getUrlToImage(), news.get(i).getContent(), news.get(i).getUrl());
                            newsList.add(studentNewsModel);
                        }

                        loadingDialog.dismiss();
                        studentNewsAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(Call<studentNewsFetcherModel> call, Throwable t) {
                        Toast.makeText(getContext(), "Failure to get API", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


    }

}




