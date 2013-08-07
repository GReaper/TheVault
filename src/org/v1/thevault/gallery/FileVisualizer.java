package org.v1.thevault.gallery;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.v1.thevault.R;



import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class FileVisualizer extends Activity 
{
	
	private File ficheroRaiz;
	private List<File> listaImagenes;
	
	//private ImageView imageView;
	private GridView grid;
	
	private Bitmap bitmapDefecto;
	private ImageAdapter adapter;
	
	private LruCache<String, Bitmap> mMemoryCache;
	
	private DiskLruCache mDiskLruCache;
	private final Object mDiskCacheLock = new Object();
	private boolean mDiskCacheStarting = true;
	private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10; // 10MB
	private static final String DISK_CACHE_SUBDIR = "thumbnails";
	private static final int DISK_CACHE_INDEX = 0;
	public CompressFormat compressFormat = CompressFormat.JPEG;
    public int compressQuality =70;
    
    private int mImageThumbSpacing;
    private int mImageThumbSize;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_visualizer);
		
		Bundle extras = getIntent().getExtras();
		ficheroRaiz= new File(extras.getString("raiz"));
		listaImagenes= getImagenes();
		
		 mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
		 mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
		
		//imageView= (ImageView) this.findViewById(R.id.imageViewTest);
		
		
		
		// Get max available VM memory, exceeding this amount will throw an
	    // OutOfMemory exception. Stored in kilobytes as LruCache takes an
	    // int in its constructor.
	    final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

	    // Use 1/8th of the available memory for this memory cache.
	    final int cacheSize = maxMemory / 8;

	    mMemoryCache = new LruCache<String, Bitmap>(cacheSize) 
	    {
	        @Override
	        protected int sizeOf(String key, Bitmap bitmap) 
	        {
	            // The cache size will be measured in kilobytes rather than
	            // number of items.
	        	
	            return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
	        }
	    };
		
	    // Initialize disk cache on background thread
	    File cacheDir = getDiskCacheDir(getBaseContext(), DISK_CACHE_SUBDIR);
	    new InitDiskCacheTask().execute(cacheDir);
	    
		
		bitmapDefecto=BitmapFactory.decodeResource(getResources(), R.drawable.empty_photo);
		
		
		
		 grid=(GridView) findViewById(R.id.grid_file_visualizer);
			
		 adapter=new ImageAdapter(getApplicationContext());					
					
		 grid.setAdapter(adapter);
		 
		 grid.getViewTreeObserver().addOnGlobalLayoutListener(
	                new ViewTreeObserver.OnGlobalLayoutListener() 
	                {
	                    @Override
	                    public void onGlobalLayout() 
	                    {
	                        if (adapter.getNumColumns() == 0)
	                        {
	                            final int numColumns = (int) Math.floor(
	                                    grid.getWidth() / (mImageThumbSize + mImageThumbSpacing));
	                            if (numColumns > 0) 
	                            {
	                                final int columnWidth =(grid.getWidth() / numColumns) - mImageThumbSpacing;
	                                adapter.setNumColumns(numColumns);
	                                adapter.setItemHeight(columnWidth);
	                                
	                            }
	                        }
	                    }
	                });
		 	
	}
	
	 public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	 {
		 final View v = inflater.inflate(R.layout.image_grid_fragment, container, false);
		 grid=(GridView) findViewById(R.id.grid_file_visualizer);
			
		 adapter=new ImageAdapter(getApplicationContext());					
					
		 grid.setAdapter(adapter);
		 
		 grid.getViewTreeObserver().addOnGlobalLayoutListener(
	                new ViewTreeObserver.OnGlobalLayoutListener() 
	                {
	                    @Override
	                    public void onGlobalLayout() 
	                    {
	                        if (adapter.getNumColumns() == 0)
	                        {
	                            final int numColumns = (int) Math.floor(
	                                    grid.getWidth() / (mImageThumbSize + mImageThumbSpacing));
	                            if (numColumns > 0) 
	                            {
	                                final int columnWidth =(grid.getWidth() / numColumns) - mImageThumbSpacing;
	                                adapter.setNumColumns(numColumns);
	                                adapter.setItemHeight(columnWidth);
	                                
	                            }
	                        }
	                    }
	                });
		 
		 return v;
			
	 }
	
	class InitDiskCacheTask extends AsyncTask<File, Void, Void> 
	{
	    @Override
	    protected Void doInBackground(File... params) 
	    {
	    	synchronized (mDiskCacheLock) 
	    	{
	            File cacheDir = params[0];
	            try {
					mDiskLruCache = DiskLruCache.open(cacheDir, 1, 1,DISK_CACHE_SIZE);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	            mDiskCacheStarting = false; // Finished initialization
	            mDiskCacheLock.notifyAll(); // Wake any waiting threads
	        }
	        return null;
	        
	    }
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) 
	{
	    if (getBitmapFromMemCache(key) == null) 
	    {
	        mMemoryCache.put(key, bitmap);
	    }
	}

	public Bitmap getBitmapFromMemCache(String key) 
	{
	    return mMemoryCache.get(key);
	}
	private List<File> getImagenes()
	{
		List<File> devolver= new ArrayList<File>();
		
		File[] imagenes=ficheroRaiz.listFiles();
		
		for(File f: imagenes)
		{
			if(FolderVisualizer.isValido(f.getName()))
			{
				
				devolver.add(f);
			}
		}		   		
		
		
		Comparator<File> comparator= new Comparator<File>() 
		{
			
			@Override
			public int compare(File lhs, File rhs) 
			{
				return rhs.getAbsolutePath().compareTo(lhs.getAbsolutePath());
			}
		};
		
		Collections.sort(devolver, comparator);
		
		//Collections.sort(devolver);
		
		return devolver;
	}

	public void loadBitmap(File file, ImageView imageView) 
	{
		final String imageKey = file.getAbsolutePath();

			final Bitmap bitmap = getBitmapFromMemCache(imageKey);
			if (bitmap != null) 
			{
				imageView.setImageBitmap(bitmap);
			}
			else 
			{
		 
				if (cancelPotentialWork(file, imageView))
				{
					final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
					final AsyncDrawable asyncDrawable =
							new AsyncDrawable(getResources(), bitmapDefecto, task);
					imageView.setImageDrawable(asyncDrawable);
					task.execute(file);
				}
			}
	}
	 
	public static int calculateInSampleSize(
	            BitmapFactory.Options options, int reqWidth, int reqHeight) 
	{
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) 
		{

	        // Calculate ratios of height and width to requested height and width
	        final int heightRatio = Math.round((float) height / (float) reqHeight);
	        final int widthRatio = Math.round((float) width / (float) reqWidth);

	        // Choose the smallest ratio as inSampleSize value, this will guarantee
	        // a final image with both dimensions larger than or equal to the
	        // requested height and width.
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
	    }

	    return inSampleSize;
	}
	 
	/**
	* Metodo que carga una imagen eficientemente
	* @param res
	* @param fichero
	* @param reqWidth
	* @param reqHeight
	* @return
	*/
	public static Bitmap decodeSampledBitmapFromResource(Resources res, File fichero,
		        int reqWidth, int reqHeight) 
	{

		    // First decode with inJustDecodeBounds=true to check dimensions
		    final BitmapFactory.Options options = new BitmapFactory.Options();
		    options.inJustDecodeBounds = true;
		    //BitmapFactory.decodeResource(res, resId, options);
		    BitmapFactory.decodeFile(fichero.getAbsolutePath(),options);
		    
		    // Calculate inSampleSize
		    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

		    // Decode bitmap with inSampleSize set
		    options.inJustDecodeBounds = false;
		    return BitmapFactory.decodeFile(fichero.getAbsolutePath(),options);
	}
	 
	/**
	* task para cargar una imagen en 2 plano
	* @author Victor
	*
	*/
	class BitmapWorkerTask extends AsyncTask<File, Void, Bitmap> 
	{
		private final WeakReference<ImageView> imageViewReference;
		private File data;

		public BitmapWorkerTask(ImageView imageView) 
		{
			// Use a WeakReference to ensure the ImageView can be garbage collected
			imageViewReference = new WeakReference<ImageView>(imageView);
		}

		// Decode image in background.
		@Override
		protected Bitmap doInBackground(File... params) 
		{
			final String imageKey = String.valueOf(params[0]);

	        // Check disk cache in background thread
	        Bitmap bitmap = getBitmapFromDiskCache(imageKey);

	        if (bitmap == null) 
	        { // Not found in disk cache
	            // Process as normal
	            bitmap = decodeSampledBitmapFromResource(
	                    getResources(), params[0], 100, 100);
	        }

	        // Add final bitmap to caches
	        addBitmapToCache(imageKey, bitmap);

	        return bitmap;
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) 
		{
			if (isCancelled()) {
				bitmap = null;
			}

			if (imageViewReference != null && bitmap != null) 
			{
				final ImageView imageView = imageViewReference.get();
				final BitmapWorkerTask bitmapWorkerTask =
						getBitmapWorkerTask(imageView);
				if (this == bitmapWorkerTask && imageView != null) 
				{
					imageView.setImageBitmap(bitmap);
				}
			}
		}
	}
	 
	public void addBitmapToCache(String data, Bitmap value) 
	{
	    // Add to memory cache as before
	    if (getBitmapFromMemCache(data) == null) 
	    {
	        mMemoryCache.put(data, value);
	    }

	    // Also add to disk cache
	    synchronized (mDiskCacheLock) 
	    {
	    	try
	    	{
	    		if (mDiskLruCache != null && mDiskLruCache.get(data) == null) 
		        {
		        	final DiskLruCache.Editor editor = mDiskLruCache.edit(data);
		        	OutputStream out = null;
		        	if (editor != null) 
		        	{
		        		
	                    out = editor.newOutputStream(DISK_CACHE_INDEX);
	                    value.compress(compressFormat, compressQuality, out);
	                    editor.commit();
	                    out.close();
	                }
		            //mDiskLruCache.put(data, value);
		        }	
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    	}
	        
	    }
	}

	public Bitmap getBitmapFromDiskCache(String key) 
	{
		
	    synchronized (mDiskCacheLock) 
	    {
	        // Wait while disk cache is started from background thread
	        while (mDiskCacheStarting) 
	        {
	            try 
	            {
	                mDiskCacheLock.wait();
	            } 
	            catch (InterruptedException e) {}
	        }
	        if (mDiskLruCache != null) 
	        {
	        	Bitmap bitmap = null;
	        	try
	        	{
	        		InputStream inputStream = null;
		        	final DiskLruCache.Snapshot snapshot = mDiskLruCache.get(key);
	                if (snapshot != null) 
	                {
	                    
	                    inputStream = snapshot.getInputStream(DISK_CACHE_INDEX);
	                    if (inputStream != null) 
	                    {
	                        FileDescriptor fd = ((FileInputStream) inputStream).getFD();

	                        // Decode bitmap, but we don't want to sample so give
	                        // MAX_VALUE as the target dimensions
	                        bitmap = decodeSampledBitmapFromDescriptor(
	                                fd, Integer.MAX_VALUE, Integer.MAX_VALUE);
	                        
	                        return bitmap;
	                    }
	                }
	        	}
	        	catch(Exception e)
	        	{
	        		e.printStackTrace();
	        	}
	            //return mDiskLruCache.get(key);
	        }
	    }
	    return null;
	}

	 public Bitmap decodeSampledBitmapFromDescriptor(
			 FileDescriptor fileDescriptor, int reqWidth, int reqHeight) 
	 {

	        // First decode with inJustDecodeBounds=true to check dimensions
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inJustDecodeBounds = true;
	        BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);

	        // Calculate inSampleSize
	        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	        // Decode bitmap with inSampleSize set
	        options.inJustDecodeBounds = false;

	        // If we're running on Honeycomb or newer, try to use inBitmap
/*
	        if (Utils.hasHoneycomb())
	        {
	            addInBitmapOptions(options,this);
	        }
*/	        

	        return BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
	    }

	
	static class AsyncDrawable extends BitmapDrawable 
	{
		private final WeakReference<BitmapWorkerTask> bitmapWorkerTaskReference;

		public AsyncDrawable(Resources res, Bitmap bitmap,
				BitmapWorkerTask bitmapWorkerTask)
		{
			super(res, bitmap);
			bitmapWorkerTaskReference =
				new WeakReference<BitmapWorkerTask>(bitmapWorkerTask);
		}

		public BitmapWorkerTask getBitmapWorkerTask()
		{
			return bitmapWorkerTaskReference.get();
		}
	}
	 
	 
	public static boolean cancelPotentialWork(File data, ImageView imageView) 
	{
		final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		if (bitmapWorkerTask != null) 
		{
			final File bitmapData = bitmapWorkerTask.data;
			if (bitmapData==null || !bitmapData.getAbsolutePath().equals(data.getAbsolutePath()))
			{
				// Cancel previous task
				bitmapWorkerTask.cancel(true);
			} else
			{
				// The same work is already in progress
				return false;
			}
		}
		// No task associated with the ImageView, or an existing task was cancelled
		return true;
	}

	private static BitmapWorkerTask getBitmapWorkerTask(ImageView imageView) 
	{
	   if (imageView != null) 
	   {
		   final Drawable drawable = imageView.getDrawable();
		   if (drawable instanceof AsyncDrawable) 
		   {
			   final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
			   return asyncDrawable.getBitmapWorkerTask();
		   }
		}
		return null;
	}

	private class ImageAdapter extends BaseAdapter
	{
		private final Context mContext;
		private int mItemHeight = 0;
        private int mNumColumns = 0;
        private GridView.LayoutParams mImageViewLayoutParams;
		 
		public ImageAdapter(Context c)
		{
			super();
			this.mContext=c;
			mImageViewLayoutParams = new GridView.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		}
		 
		@Override
		public int getCount() 
		{
			return listaImagenes.size();
		}

		@Override
		public File getItem(int position) 
		{
			return listaImagenes.get(position);
		}

		@Override
		public long getItemId(int position) 
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			ImageView imageView;
            if (convertView == null)
            { // if it's not recycled, initialize some attributes
                imageView = new ImageView(mContext);                            
                imageView.setLayoutParams(mImageViewLayoutParams);
                imageView.setScaleType(ScaleType.FIT_XY);
            } else 
            {
                imageView = (ImageView) convertView;
            }
            
            if (imageView.getLayoutParams().height != mItemHeight) 
            {
                imageView.setLayoutParams(mImageViewLayoutParams);
                imageView.setScaleType(ScaleType.FIT_XY);
            }

            loadBitmap(getItem(position), imageView);
            return imageView;
		}
		
        public void setNumColumns(int numColumns) 
        {
            mNumColumns = numColumns;
        }

        public int getNumColumns() 
        {
            return mNumColumns;
        }
        
        public void setItemHeight(int height) 
        {
            if (height == mItemHeight) 
            {
                return;
            }
            mItemHeight = height;
            mImageViewLayoutParams = new GridView.LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);            
            notifyDataSetChanged();
        }
		 
	}


	
	
    /**
     * Get a usable cache directory (external if available, internal otherwise).
     *
     * @param context The context to use
     * @param uniqueName A unique directory name to append to the cache dir
     * @return The cache dir
     */
    public static File getDiskCacheDir(Context context, String uniqueName) 
    {
        // Check if media is mounted or storage is built-in, if so, try and use external cache dir
        // otherwise use internal cache dir
        final String cachePath =
                Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                        !isExternalStorageRemovable() ? getExternalCacheDir(context).getPath() :
                                context.getCacheDir().getPath();

        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * Check if external storage is built-in or removable.
     *
     * @return True if external storage is removable (like an SD card), false
     *         otherwise.
     */
    @TargetApi(9)
    public static boolean isExternalStorageRemovable() 
    {
        if (Utils.hasGingerbread()) 
        {
            return Environment.isExternalStorageRemovable();
        }
        return true;
    }

    /**
     * Get the external app cache directory.
     *
     * @param context The context to use
     * @return The external cache dir
     */
    @TargetApi(8)
    public static File getExternalCacheDir(Context context) 
    {
        if (Utils.hasFroyo()) 
        {
            return context.getExternalCacheDir();
        }

        // Before Froyo we need to construct the external cache dir ourselves
        final String cacheDir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + cacheDir);
    }







}
