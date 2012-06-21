package dk.nezbo.traveljournal;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;

public class GalleryImageAdapter extends BaseAdapter {
    int mGalleryItemBackground;
    private Context mContext;
    
    private ArrayList<AdvImage> images;

    public GalleryImageAdapter(Context c, ArrayList<AdvImage> images) {
        mContext = c;
        mGalleryItemBackground = R.color.blue;
        this.images = images;
    }

    public int getCount() {
        return images.size();
    }

	public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        Bitmap bitmap = images.get(position).getBitmap(mContext);
        imageView.setImageBitmap(bitmap);
        //imageView.setImageResource(mImageIds[position]);
        
        imageView.setLayoutParams(new Gallery.LayoutParams(200, 150));
        imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        imageView.setBackgroundResource(mGalleryItemBackground);

        return imageView;
    }
}