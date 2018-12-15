package com.dude.rohithgilla.gillsblog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private RecyclerView blogListView;
    private List<Blogpost> blogpostList;
    private FirebaseFirestore firebaseFirestore;
    private BlogRecyclerAdapter blogRecyclerAdapter;
    private FirebaseAuth firebaseAuth;
    private DocumentSnapshot lastVisible;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Inflate the layout for this fragment
        blogpostList = new ArrayList<>();
        blogListView = view.findViewById(R.id.blogListView);
        firebaseAuth = FirebaseAuth.getInstance();
        blogRecyclerAdapter = new BlogRecyclerAdapter(blogpostList);
        blogListView.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blogListView.setAdapter(blogRecyclerAdapter);
        if( firebaseAuth.getCurrentUser() != null) {
            blogListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    Boolean isReachedBottom = !recyclerView.canScrollVertically(1);
                    if (isReachedBottom) {
                        String descLast = lastVisible.getString("desc");
                        Toast.makeText(container.getContext(),descLast,Toast.LENGTH_LONG).show();
                        loadMore();
                    }
                }
            });
            firebaseFirestore = FirebaseFirestore.getInstance();
            Query firstQuery = firebaseFirestore.collection("Posts").orderBy("timestamp",Query.Direction.DESCENDING);
            firstQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size()-1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            Blogpost blogpost = doc.getDocument().toObject(Blogpost.class);
                            blogpostList.add(blogpost);
                            blogRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });

        }

        return view;
    }

    public void loadMore(){
        Query nextQuery = firebaseFirestore.collection("Posts")
                .orderBy("timestamp",Query.Direction.DESCENDING)
                .startAfter(lastVisible)
                .limit(3);

        nextQuery.addSnapshotListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                if (!documentSnapshots.isEmpty()) {
                    lastVisible = documentSnapshots.getDocuments().get(documentSnapshots.size() - 1);
                    for (DocumentChange doc : documentSnapshots.getDocumentChanges()) {
                        if (doc.getType() == DocumentChange.Type.ADDED) {
                            Blogpost blogpost = doc.getDocument().toObject(Blogpost.class);
                            blogpostList.add(blogpost);
                            blogRecyclerAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        });
    }

}
