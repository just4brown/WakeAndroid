package com.lucerlabs.wake;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FullScreenImageAdapter extends PagerAdapter {

	private LayoutInflater mInflater;
	private ArrayList<String> mImagePaths;

	public FullScreenImageAdapter(LayoutInflater inflater, ArrayList<String> imagePaths) {
		this.mInflater = inflater;
		this.mImagePaths = imagePaths;
	}

	@Override
	public int getCount() {
		return this.mImagePaths.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		ImageView imgDisplay;

		View viewLayout = mInflater.inflate(R.layout.fullscreen_image, container,
				false);

		imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);

		Bitmap bitmap = getBitmapFromAsset(mInflater.getContext(), mImagePaths.get(position));
		imgDisplay.setImageBitmap(bitmap);

		((ViewPager) container).addView(viewLayout);

		return viewLayout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);

	}

	private static Bitmap getBitmapFromAsset(Context context, String filePath) {
		AssetManager assetManager = context.getAssets();
		InputStream istr;
		Bitmap bitmap = null;
		try {
			istr = assetManager.open(filePath);
			bitmap = BitmapFactory.decodeStream(istr);
		} catch (IOException e) {
			// handle exception
		}

		return bitmap;
	}
}
