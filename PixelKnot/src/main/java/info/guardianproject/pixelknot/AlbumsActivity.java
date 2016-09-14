package info.guardianproject.pixelknot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import info.guardianproject.pixelknot.adapters.AlbumAdapter;
import info.guardianproject.pixelknot.adapters.PhotoAdapter;

public class AlbumsActivity extends ActivityBase implements AlbumAdapter.AlbumAdapterListener, PhotoAdapter.PhotoAdapterListener {

    private static final boolean LOGGING = false;
    private static final String LOGTAG = "AlbumsActivity";

    private View mRootView;
    private RecyclerView mRecyclerView;
    private AlbumLayoutManager mLayoutManager;
    private View mLayoutGalleryInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_albums);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mRootView = findViewById(R.id.main_content);

        mLayoutGalleryInfo = mRootView.findViewById(R.id.gallery_info);
        Button btnOk = (Button) mLayoutGalleryInfo.findViewById(R.id.btnGalleryInfoOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                App.getInstance().getSettings().setSkipGalleryInfo(true);
                mLayoutGalleryInfo.setVisibility(View.GONE);
            }
        });
        mLayoutGalleryInfo.setVisibility(View.GONE);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_albums);
        int colWidth = getResources().getDimensionPixelSize(R.dimen.photo_column_size);
        mLayoutManager = new AlbumLayoutManager(this, colWidth);
        mRecyclerView.setLayoutManager(mLayoutManager);

        setAlbumAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mRecyclerView != null && mRecyclerView.getAdapter() instanceof PhotoAdapter) {
            ((PhotoAdapter) mRecyclerView.getAdapter()).update();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (mRecyclerView.getAdapter() instanceof PhotoAdapter) {
                setAlbumAdapter();
                return true;
            }
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAlbumSelected(String album) {
        setPhotosAdapter(album, false, false);
    }

    @Override
    public void onPhotoSelected(String photo, final View thumbView) {
        //final Uri uri = Uri.parse(photo);
        Intent data = new Intent();
        data.putExtra("uri", photo);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onCameraSelected() {
    }

    @Override
    public void onAlbumsSelected() {
    }

    @Override
    public void onBackPressed() {
        if (mRecyclerView.getAdapter() instanceof PhotoAdapter) {
            setAlbumAdapter();
            return;
        }
        super.onBackPressed();
    }

    private void setAlbumAdapter() {
        getSupportActionBar().setTitle(R.string.title_albums);
        mRecyclerView.setLayoutManager(mLayoutManager);
        AlbumAdapter adapter = new AlbumAdapter(this);
        adapter.setListener(this);
        int colWidth = getResources().getDimensionPixelSize(R.dimen.album_column_size);
        mLayoutManager.setColumnWidth(colWidth);
        mRecyclerView.setAdapter(adapter);
    }

    private void setPhotosAdapter(String album, boolean showCamera, boolean showAlbums) {
        getSupportActionBar().setTitle(album);
        mRecyclerView.setLayoutManager(mLayoutManager);
        PhotoAdapter adapter = new PhotoAdapter(this, album, showCamera, showAlbums);
        adapter.setListener(this);
        int colWidth = getResources().getDimensionPixelSize(R.dimen.photo_column_size);
        mLayoutManager.setColumnWidth(colWidth);
        mRecyclerView.setAdapter(adapter);
    }
}
