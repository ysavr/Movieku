package com.mythcon.savr.movieku;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mythcon.savr.movieku.Interface.ItemClickListener;
import com.mythcon.savr.movieku.Model.Movie;
import com.mythcon.savr.movieku.ViewHolder.MovieViewHolder;
import com.rengwuxian.materialedittext.MaterialEditText;

import info.hoang8f.widget.FButton;


public class MainActivity extends AppCompatActivity {
    CoordinatorLayout rootLayout;
    MaterialEditText edtMovieName,edtDescription,edtRate,edtGenre;
    FButton btnSelect,btnUpload;

    FirebaseDatabase database;
    DatabaseReference movie;
    FirebaseStorage storage;
    StorageReference storageReference;
    FirebaseRecyclerAdapter<Movie,MovieViewHolder> adapter;

    RecyclerView recyclerMovie;
    RecyclerView.LayoutManager layoutManager;

    Movie newMovie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        database = FirebaseDatabase.getInstance();
        movie = database.getReference("Movie");

        rootLayout = findViewById(R.id.rootLayout);
        recyclerMovie = findViewById(R.id.recycler_movie);

        recyclerMovie.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerMovie.setLayoutManager(layoutManager);
        loadMovie();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddDialog();
            }
        });
    }

    private void loadMovie() {
        adapter = new FirebaseRecyclerAdapter<Movie, MovieViewHolder>(Movie.class,
                R.layout.view_holder_movie,
                MovieViewHolder.class,movie) {
            @Override
            protected void populateViewHolder(MovieViewHolder viewHolder, final Movie model, int position) {
                viewHolder.movie_name_text.setText(model.getName());

                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onclick(View view, int position, boolean isLongClick) {
                        Toast.makeText(MainActivity.this, ""+model.getName(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        };
        adapter.notifyDataSetChanged();
        recyclerMovie.setAdapter(adapter);
    }

    private void showAddDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("Tambahkan Movie");
        alertDialog.setMessage("Silahkan isikan informasi dengan benar");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View add_new_movie_layout = layoutInflater.inflate(R.layout.add_new_movie_layout,null);

        edtMovieName = add_new_movie_layout.findViewById(R.id.edtMovieName);
        edtDescription = add_new_movie_layout.findViewById(R.id.edtDescription);
        edtRate = add_new_movie_layout.findViewById(R.id.edtRate);
        edtGenre = add_new_movie_layout.findViewById(R.id.edtGenre);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (newMovie == null){
                    newMovie = new Movie(
                            edtMovieName.getText().toString(),
                            edtDescription.getText().toString(),
                            edtRate.getText().toString(),
                            edtGenre.getText().toString());
                    movie.push().setValue(newMovie);
                    Snackbar.make(rootLayout,"Film "+newMovie.getName()+" berhasil ditambahkan",Snackbar.LENGTH_SHORT).show();
                }else {
                    dialogInterface.dismiss();
                    movie.push().setValue(newMovie);
                    Snackbar.make(rootLayout, "Film " + newMovie.getName() + " berhasil ditambahkan lho", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.setView(add_new_movie_layout);
        alertDialog.show();
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals("Update"))
        {
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals("Delete")) {
            deleteDialog(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    private void deleteDialog(final String key) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Delete Movie");
        alertDialog.setMessage("Are you sure to delete the movie ?");

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                movie.child(key).removeValue();
                Toast.makeText(MainActivity.this, "Movie deleted !!", Toast.LENGTH_SHORT).show();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    private void showUpdateDialog(final String key, final Movie item) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Update Movie");
        alertDialog.setMessage("Please fill full information");

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View add_new_movie_layout = layoutInflater.inflate(R.layout.add_new_movie_layout,null);

        edtMovieName = add_new_movie_layout.findViewById(R.id.edtMovieName);
        edtDescription = add_new_movie_layout.findViewById(R.id.edtDescription);
        edtRate = add_new_movie_layout.findViewById(R.id.edtRate);
        edtGenre = add_new_movie_layout.findViewById(R.id.edtGenre);

        edtMovieName.setText(item.getName());
        edtDescription.setText(item.getDescription());
        edtRate.setText(item.getRate());
        edtGenre.setText(item.getGenre());

        alertDialog.setView(add_new_movie_layout);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                item.setName(edtMovieName.getText().toString());
                item.setDescription(edtDescription.getText().toString());
                item.setGenre(edtGenre.getText().toString());
                item.setRate(edtRate.getText().toString());

                movie.child(key).setValue(item);
                Snackbar.make(rootLayout,""+item.getName()+" telah diubah",Snackbar.LENGTH_SHORT).show();
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
