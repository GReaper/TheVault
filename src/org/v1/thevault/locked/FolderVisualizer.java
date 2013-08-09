package org.v1.thevault.locked;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.v1.thevault.R;
import org.v1.thevault.gallery.FileVisualizer;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class FolderVisualizer extends Activity 
{
	
	ListView listaElementos;
	AdapterSD adapterSD;
	File raizSD;
	List<File> ficherosRaiz;
	View vistaVacia;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.folder_lock_visualizer_layout);
		
		listaElementos=(ListView) this.findViewById(R.id.listaSD_lock);
		
				
		raizSD=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),".thevault");		
		if(!raizSD.exists())
		{
			raizSD.mkdirs();
		}
		
		
		ficherosRaiz= construyeArbol(raizSD);		
		
			
		adapterSD= new AdapterSD(getApplicationContext());
		listaElementos.setAdapter(adapterSD);	
		
		vistaVacia=this.findViewById(R.id.emptyView);
		
		listaElementos.setEmptyView(vistaVacia);
		
		listaElementos.setOnLongClickListener(getListener());
		vistaVacia.setOnLongClickListener(getListener());
		
	}
		
	public List<File> construyeArbol(File ficheroAMirar)
	{
		List<File> devolver= new ArrayList<File>();
		
		File[] hijos= ficheroAMirar.listFiles();
		
		for(File f: hijos)
		{
			devolver.add(f);
		}		
		
		return devolver;
	}
	
	public void crearCarpeta()
	{
		File faux= new File(raizSD, "prueba");
		if(!faux.exists())
		{
			faux.mkdirs();
		}
	}
	
	public OnLongClickListener getListener()
	{
		return new OnLongClickListener()
		{
			
			@Override
			public boolean onLongClick(View v) 
			{
				crearCarpeta();
				ficherosRaiz= construyeArbol(raizSD);	
				adapterSD.notifyDataSetChanged();
				return true;
			}
		};
	}
	
	//XXX adapter
	public class AdapterSD extends BaseAdapter
	{
		Context contexto;
		
		public class ViewHolder
		{ 
		    protected TextView textoFichero;
		    protected ImageView imageFichero;
		    protected RelativeLayout layout;
		   
		}
		
		public AdapterSD(Context c)
		{
			this.contexto=c;
		}
		
		
		@Override
		public int getCount() 
		{
			return ficherosRaiz.size();
		}

		@Override
		public File getItem(int pos) 
		{
			return ficherosRaiz.get(pos);
		}

		@Override
		public long getItemId(int pos)
		{
			return pos;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) 
		{
			View view = null;
			ViewHolder holder;
			/*
			if(convertView==null)
			{
				LayoutInflater inflator= LayoutInflater.from(contexto);
				view=inflator.inflate(R.layout.item_sd, null);
				holder= new ViewHolder();
				holder.imageFichero=(ImageView) view.findViewById(R.id.imageSD);
				holder.textoFichero= (TextView) view.findViewById(R.id.pathSD);
				holder.layout=(RelativeLayout) view.findViewById(R.id.layoutSD);
				view.setTag(holder);
			}
			else
			{
				holder = (ViewHolder) convertView.getTag();
			}
			*/
			LayoutInflater inflator= LayoutInflater.from(contexto);
			view=inflator.inflate(R.layout.item_sd, null);
			holder= new ViewHolder();
			holder.imageFichero=(ImageView) view.findViewById(R.id.imageSD);
			holder.textoFichero= (TextView) view.findViewById(R.id.pathSD);
			holder.layout=(RelativeLayout) view.findViewById(R.id.layoutSD);
			view.setTag(holder);
			
			final File ficheroActual= getItem(position);

			
			holder.imageFichero.setImageDrawable(getResources().getDrawable(R.drawable.carpeta));
			
									
			holder.textoFichero.setText(ficheroActual.getName());
			
			holder.layout.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent(FolderVisualizer.this, FileVisualizer.class);
					intent.putExtra("raiz", ficheroActual.getAbsolutePath());
					startActivity(intent);
					
				}
			});
			
			return view;
		}
		
	}


}
