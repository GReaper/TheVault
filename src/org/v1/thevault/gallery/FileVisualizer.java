package org.v1.thevault.gallery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.classes.AESEncryption;
import org.v1.thevault.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.util.LruCache;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

public class FileVisualizer  extends Activity 
{
	
	private ImageAdapter adapter;
	private File destino;
    private Button botonOcultar;
    private File ficheroRaiz;
    private List<MyFile> listaImagenes;
    private Bitmap bitmapDefecto;
    private GridView grid;
    
    public static int tamBuffer=1024;
    
    private int mImageThumbSpacing;
    private int mImageThumbSize;
    

	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_file_visualizer_gallery);
		System.gc();
		
		Bundle extras = getIntent().getExtras();
		ficheroRaiz= new File(extras.getString("raiz"));
		destino= new File(extras.getString("destino"));
		listaImagenes= getImagenes();
		
		bitmapDefecto=BitmapFactory.decodeResource(getResources(), R.drawable.empty_photo);
		
		 grid=(GridView) findViewById(R.id.grid_file_visualizer_gallery);
			
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
		 	
		 botonOcultar= (Button) findViewById(R.id.botonOcultarGallery);
		 botonOcultar.setOnClickListener(new OnClickListener() 
		 {
			
			@Override
			public void onClick(View v) 
			{
				MoveFiles mf= new MoveFiles();
				mf.execute();
				
			}
		});
		 
	
		mImageThumbSize = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_size);
		mImageThumbSpacing = getResources().getDimensionPixelSize(R.dimen.image_thumbnail_spacing);
		
	}
	
	 public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) 
	 {

		 final View v = inflater.inflate(R.layout.activity_file_visualizer_gallery, container, false);
		 grid=(GridView) findViewById(R.id.grid_file_visualizer_gallery);
			
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
	 
	 private List<MyFile> getImagenes()
		{
			List<MyFile> devolver= new ArrayList<MyFile>();
			
			File[] imagenes=ficheroRaiz.listFiles();
			
			for(File f: imagenes)
			{
				if(FolderVisualizer.isValido(f.getName()))
				{
					MyFile faux= new MyFile(f);
					
					devolver.add(faux);
				}
			}		   		
			
			
			Comparator<MyFile> comparator= new Comparator<MyFile>() 
			{
				
				@Override
				public int compare(MyFile lhs, MyFile rhs) 
				{
					return rhs.getFile().getAbsolutePath().compareTo(lhs.getFile().getAbsolutePath());
				}
			};
			
			Collections.sort(devolver, comparator);
			
			//Collections.sort(devolver);
			
			return devolver;
		}

	 //XXX especifico de la clase
	 
	 public static int calculateInSampleSize(
	            BitmapFactory.Options options, int reqWidth, int reqHeight) {
	    // Raw height and width of image
	    final int height = options.outHeight;
	    final int width = options.outWidth;
	    int inSampleSize = 1;

	    if (height > reqHeight || width > reqWidth) {

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
	
	 class BitmapWorkerTask extends AsyncTask<MyFile, Void, Bitmap> 
	 {
		    private final WeakReference<ImageView> imageViewReference;
			public MyFile data;
		    

		    public BitmapWorkerTask(ImageView imageView) {
		        // Use a WeakReference to ensure the ImageView can be garbage collected
		        imageViewReference = new WeakReference<ImageView>(imageView);
		    }

		    // Decode image in background.
		    @Override
		    protected Bitmap doInBackground(MyFile... params) 
		    {
		        data = params[0];
		        return decodeSampledBitmapFromResource(getResources(), data, 100, 100);
		    }

		    // Once complete, see if ImageView is still around and set bitmap.
		    @Override
		    protected void onPostExecute(Bitmap bitmap) 
		    {
		        if (isCancelled()) 
		        {
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
	 
	 public void loadBitmap(MyFile resId, ImageView imageView) 
	 {
		    if (cancelPotentialWork(resId, imageView)) {
		        final BitmapWorkerTask task = new BitmapWorkerTask(imageView);
		        final AsyncDrawable asyncDrawable =
		                new AsyncDrawable(getResources(), bitmapDefecto, task);
		        imageView.setImageDrawable(asyncDrawable);
		        task.execute(resId);
		    }
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
	 public static boolean cancelPotentialWork(MyFile data, ImageView imageView) 
	 {
		    final BitmapWorkerTask bitmapWorkerTask = getBitmapWorkerTask(imageView);

		    if (bitmapWorkerTask != null) 
		    {
		    	final MyFile bitmapData = bitmapWorkerTask.data;
				if (bitmapData==null || !bitmapData.getFile().getAbsolutePath().equals(data.getFile().getAbsolutePath()))
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
		   if (imageView != null) {
		       final Drawable drawable = imageView.getDrawable();
		       if (drawable instanceof AsyncDrawable) {
		           final AsyncDrawable asyncDrawable = (AsyncDrawable) drawable;
		           return asyncDrawable.getBitmapWorkerTask();
		       }
		    }
		    return null;
	 }
	 public static Bitmap decodeSampledBitmapFromResource(Resources res, MyFile fichero,
	        int reqWidth, int reqHeight) 
	{

	    // First decode with inJustDecodeBounds=true to check dimensions
	    final BitmapFactory.Options options = new BitmapFactory.Options();
	    options.inJustDecodeBounds = true;
	    //BitmapFactory.decodeResource(res, resId, options);
	    BitmapFactory.decodeFile(fichero.getFile().getAbsolutePath(),options);
	    
	    // Calculate inSampleSize
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;
	    return BitmapFactory.decodeFile(fichero.getFile().getAbsolutePath(),options);
	}
	
	 //XXX adapter
	 private class ImageAdapter extends BaseAdapter
		{
			private final Context mContext;
			private int mItemHeight = 0;
	        private int mNumColumns = 0;
	        private LayoutParams mImageViewLayoutParams;
	        private LayoutInflater inflator;
	        
	        public class ViewHolder
			{ 
			    protected ImageView imagenPpal;
			    protected ImageView imagenSecundaria;
			    
			}
			 
			public ImageAdapter(Context c)
			{
				super();
				this.mContext=c;
				this.inflator = LayoutInflater.from(this.mContext);
				mImageViewLayoutParams = new GridView.LayoutParams(
	                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
			}
			 
			@Override
			public int getCount() 
			{
				return listaImagenes.size();
			}

			@Override
			public MyFile getItem(int position) 
			{
				return listaImagenes.get(position);
			}

			@Override
			public long getItemId(int position) 
			{
				return position;
			}

			@Override
			public View getView(final int position, View convertView, ViewGroup parent) 
			{
				View view = null;
				final ViewHolder viewHolder;
				
				
				if(convertView==null)
				{
					view=inflator.inflate(R.layout.image_gallery, null);
					viewHolder = new ViewHolder();
									
					viewHolder.imagenPpal=(ImageView)view.findViewById(R.id.imageGalleryBackground);
					viewHolder.imagenSecundaria=(ImageView)view.findViewById(R.id.imageGalleryHover);
					
					//viewHolder.imagenPpal.setLayoutParams(mImageViewLayoutParams);

					int width=mImageViewLayoutParams.width;
					int height=mImageViewLayoutParams.height;
					
					viewHolder.imagenPpal.setScaleType(ScaleType.FIT_XY);
					viewHolder.imagenPpal.getLayoutParams().width=width;
					viewHolder.imagenPpal.getLayoutParams().height=height;
					
					//viewHolder.imagenSecundaria.setLayoutParams(mImageViewLayoutParams);
					viewHolder.imagenSecundaria.getLayoutParams().width=width;
					viewHolder.imagenSecundaria.getLayoutParams().height=height;
					viewHolder.imagenSecundaria.setScaleType(ScaleType.FIT_XY);
					
					view.setTag(viewHolder);
				}			
				else
				{
					view=convertView;
					viewHolder=(ViewHolder) convertView.getTag();
				}
				
				if(viewHolder.imagenPpal.getLayoutParams().height != mItemHeight && mItemHeight!=0)
				{
					int width=mImageViewLayoutParams.width;
					int height=mImageViewLayoutParams.height;
					
					viewHolder.imagenPpal.getLayoutParams().width=width;
					viewHolder.imagenPpal.getLayoutParams().height=height;
					//viewHolder.imagenPpal.setLayoutParams(mImageViewLayoutParams);
					viewHolder.imagenPpal.setScaleType(ScaleType.FIT_XY);
					
					//viewHolder.imagenSecundaria.setLayoutParams(mImageViewLayoutParams);
					viewHolder.imagenSecundaria.getLayoutParams().width=width;
					viewHolder.imagenSecundaria.getLayoutParams().height=height;
					viewHolder.imagenSecundaria.setScaleType(ScaleType.FIT_XY);
				}
				//viewHolder.imagenPpal.setBackground(getResources().getDrawable(R.drawable.ic_launcher));
				
				final MyFile myfile=getItem(position);

	            loadBitmap(myfile, viewHolder.imagenPpal);
	            
	            if(myfile.isSeleccionado())
	            {
	            	
	            	viewHolder.imagenSecundaria.setBackgroundColor(getResources().getColor(R.color.selection));
	            }
	            else
	            {
	            	viewHolder.imagenSecundaria.setBackgroundColor(Color.TRANSPARENT);	
	            }            
	            
	            viewHolder.imagenSecundaria.setOnClickListener(new OnClickListener() 
	            {
					
					@Override
					public void onClick(View v) 
					{
						myfile.cambiarSeleccion();
											
						if(myfile.isSeleccionado())
			            {
							viewHolder.imagenSecundaria.setBackgroundColor(getResources().getColor(R.color.selection));		            	
			            }
			            else
			            {
			            	viewHolder.imagenSecundaria.setBackgroundColor(Color.TRANSPARENT);	
			            }
						
					}
				});

	            return view;
	           
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
	            mImageViewLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, mItemHeight);            
	            notifyDataSetChanged();
	        }
			 
		}

	 class MoveFiles extends AsyncTask<Void, Void, Void>
	    {
	    	ProgressDialog progressBar;
	    	List<File> ficherosAMover;
	    	
	    	@Override
	    	protected void onPreExecute()
	    	{
	    		ficherosAMover= new ArrayList<File>();
	    		
	    		for(MyFile mf:listaImagenes)
	    		{
	    			if(mf.isSeleccionado())
	    				ficherosAMover.add(mf.getFile());
	    		}
	    		
	    		progressBar = new ProgressDialog(FileVisualizer.this);
				progressBar.setCancelable(true);
				progressBar.setMessage(getResources().getString(R.string.file_moving));
				progressBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
				progressBar.setProgress(0);
				progressBar.setMax(ficherosAMover.size());
				if(ficherosAMover.size()!=0)
					progressBar.show();
	    	}
	    	
			@Override
			protected Void doInBackground(Void... params) 
			{
				int progreso=1;
				for(File f: ficherosAMover)
				{
						try 
						{
							moverFichero(f);
						} 
						catch (Exception e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						progressBar.setProgress(progreso);
						progreso++;
					
				}
				
				return null;
			}
	    	
			@Override
			protected void onPostExecute(Void v)
			{
				listaImagenes= getImagenes();
				adapter.notifyDataSetChanged();
				if(Environment.getExternalStorageDirectory()!=null)
				{
					//refrescamos la galeria para que se oculten de verdad
					sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory())));
					
				}
				
				progressBar.dismiss();
			}
			
			public void moverFichero(File fichero)throws Exception
			{
				/*
				String nombreArchivo=fichero.getName()+".lck";			
				
				String bla=Base64.encodeToString(nombreArchivo.getBytes(), Base64.DEFAULT);
				
				byte[] auxiliar= Base64.decode(bla, Base64.DEFAULT);
				
				String destino = new String(auxiliar, "UTF-8");
				
				
				File destinoAuxiliar= new File(destino, nombreArchivo);		
				
				*/
				
				String rutaAbsoluta= fichero.getAbsolutePath();
				String nombreTransformado=Base64.encodeToString(rutaAbsoluta.getBytes(), Base64.DEFAULT);
				
				//quitamos el /n
				nombreTransformado=nombreTransformado.substring(0, nombreTransformado.length()-1);

				String nombreArchivo=nombreTransformado+".lck";
				File destinoAuxiliar= new File(destino, nombreArchivo);	
				
				InputStream inStream = null;
			    OutputStream outStream = null;
			    
			    inStream = new FileInputStream(fichero);
	            outStream = new FileOutputStream(destinoAuxiliar);
	            
			    
			    byte[] buffer = new byte[tamBuffer];
			    List<Byte> listaBytes= new ArrayList<Byte>();
			    	           
	            while ((inStream.read(buffer)) > 0)
	            {
	                for(byte b: buffer)
	                {
	                	listaBytes.add(b);
	                }
	            }
	            byte[] bytesCodificados=new byte[listaBytes.size()];
	            for(int i=0;i<listaBytes.size();i++)
	            {
	            	bytesCodificados[i]=listaBytes.get(i);
	            }
	            
	            //limpieza
	            listaBytes=null; 
	            System.gc();
	            
	            //XXX codificacion
	            //bytesCodificados= AESEncryption.encryptFromBytesToBytes(bytesCodificados);
	            
	            outStream.write(bytesCodificados, 0, bytesCodificados.length);
	            
	 
	            if (inStream != null)inStream.close();
	            if (outStream != null)outStream.close();
	            
	          
	            fichero.delete();
	 
			}
	    }
	   
	 @Override
	 public void onBackPressed()
	 {
		 super.onBackPressed();
		 Intent intent = new Intent(FileVisualizer.this, FolderVisualizer.class);
		 intent.putExtra("destino", destino.getAbsolutePath());
		 startActivity(intent);
		 finish();
	 }

}
