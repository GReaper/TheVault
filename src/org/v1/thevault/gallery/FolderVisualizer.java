package org.v1.thevault.gallery;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.v1.thevault.R;


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
	File ficheroEnPantalla;
	
	public static String[] extensionesValidas;
	
	List<Folder> ficherosRaiz;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.folder_visualizer_layout);
		
		listaElementos=(ListView) this.findViewById(R.id.listaSD);
		
		raizSD=new File(Environment.getExternalStorageDirectory().getAbsolutePath());
		
		extensionesValidas= getExtensiones();
		
		ficherosRaiz= construyeArbol(raizSD, new ArrayList<Folder>());
		
		Collections.sort(ficherosRaiz);
			
		adapterSD= new AdapterSD(getApplicationContext());
		listaElementos.setAdapter(adapterSD);		
		
	}
	
	private String[] getExtensiones()
	{
		
		String[] devolver= new String[3];
		
		devolver[0]=".jpg";
		devolver[1]=".jpeg";
		devolver[2]=".png";
		
		return devolver;
		
	}
	
	public List<Folder> construyeArbol(File ficheroAMirar,List<Folder> listaActual)
	{
		if(ficheroAMirar.isFile())
		{
			return listaActual;
		}
		else
		{
			List<Folder> aDevolver= new ArrayList<Folder>(listaActual);
			
			if(ficheroAMirar.isDirectory())
			{
				
				if(ficheroAMirar.list().length>0)//comprobamos si esta vacio
				{
					File[] hijosArray= ficheroAMirar.listFiles();					
					
					List<File> hijos= new ArrayList<File>();
					for(File f: hijosArray)
					{
						hijos.add(f);
					}
					
					Comparator<File> comparator= new Comparator<File>() 
					{
						
						@Override
						public int compare(File lhs, File rhs) 
						{
							return lhs.getName().compareTo(rhs.getName());
						}
					};
					
					Collections.sort(hijos,comparator);
							
					//construimos la lista para cada hijo
					boolean hayHijos=false;
					File ultimoHijo=null;
					for(File hijoConcreto: hijos)
					{
						if(!hijoConcreto.getName().toLowerCase().startsWith("."))//no analizamos ocultos
						{
							aDevolver= construyeArbol(hijoConcreto, aDevolver);
							
							if(isValido(hijoConcreto.getName()))
							{
								hayHijos=true;
								ultimoHijo=hijoConcreto;
							}
						}
						
						//si hay un archivo que dice que no se analice, se devuelve lo que teniamos
						if(hijoConcreto.getName().toLowerCase().equals(".nomedia"))
						{
							return listaActual;
						}
						
					}			
					
					if(hayHijos)
					{
						Folder folder= new Folder(ficheroAMirar);
						folder.setImagen(ultimoHijo);
						aDevolver.add(folder);
					}
				}
				
				
			}		
			
			return aDevolver;
		}
	}
	
	public static boolean isValido(String name) 
	{
		
		for(String s: extensionesValidas)
		{
			if(name.toLowerCase().endsWith(s))
			{
				return true;
			}
		}
		
		
		return false;
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
		public Folder getItem(int pos) 
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
			
			final Folder ficheroActual= getItem(position);
			
			//TODO poner la imagen de la 1 foto que aparece
			Bitmap imagen= BitmapFactory.decodeFile(ficheroActual.getImagen().getAbsolutePath());
			holder.imageFichero.setImageBitmap(imagen);
			//holder.imageFichero.setImageDrawable(getResources().getDrawable(R.drawable.ic_launcher));			
						
			holder.textoFichero.setText(ficheroActual.getCarpeta().getName());
			
			holder.layout.setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v) 
				{
					Intent intent = new Intent(FolderVisualizer.this, FileVisualizer.class);
					intent.putExtra("raiz", ficheroActual.getCarpeta().getAbsolutePath());
					startActivity(intent);
					
				}
			});
			
			return view;
		}
		
	}


}
