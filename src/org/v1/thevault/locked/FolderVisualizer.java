package org.v1.thevault.locked;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.v1.thevault.R;



import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FolderVisualizer extends Activity 
{
	
	ListView listaElementos;
	AdapterSD adapterSD;
	File raizSD;
	List<File> ficherosRaiz;
	View vistaVacia;
	LinearLayout layout;
	private static final int DIALOGO_OPCIONES = 1;
	private File fileSeleccionado;

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
		
		layout=(LinearLayout) findViewById(R.id.layout_lock_visualizer);
		layout.setOnClickListener(getListener());
		
		vistaVacia.setOnClickListener(getListener());
		
	}
		
	public List<File> construyeArbol(File ficheroAMirar)
	{
		List<File> devolver= new ArrayList<File>();
		
		File[] hijos= ficheroAMirar.listFiles();
		
		for(File f: hijos)
		{
			devolver.add(f);
		}		
		
		Comparator<File> comparator= new Comparator<File>() 
		{

			@Override
			public int compare(File lhs, File rhs) 
			{
				return lhs.getName().toLowerCase().compareTo(rhs.getName().toLowerCase());
			}
		};
		
		Collections.sort(devolver, comparator);
		
		
		return devolver;
	}
	
	public void crearCarpeta()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(R.string.newFolder);
		alert.setMessage(R.string.newName);

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
			  String value = input.getText().toString();
			  File faux= new File(raizSD, value);
				if(!faux.exists())
				{
					faux.mkdirs();
					ficherosRaiz= construyeArbol(raizSD);	
					adapterSD.notifyDataSetChanged();
				}
			}
		});

		
		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() 
		{
		  public void onClick(DialogInterface dialog, int whichButton) 
		  {
		   
		  }
		});

		alert.show();
		
		
	}
	
	public void borrarCarpeta()
	{
		int numeroHijos=fileSeleccionado.listFiles().length;
    	if(numeroHijos!=0)
    	{
    		//hay hijos, no se borra la carpeta
    		Toast.makeText(getApplicationContext(), R.string.cantdelete, Toast.LENGTH_LONG).show();
    	}
    	else
    	{
    		fileSeleccionado.delete();
    	}
	}
	
	public void renombrarCarpeta()
	{
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle(R.string.editFolder);
		alert.setMessage(R.string.newName);

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() 
		{
			public void onClick(DialogInterface dialog, int whichButton) 
			{
			  String value = input.getText().toString();
			  File faux= new File(raizSD, value);
				if(!faux.exists())
				{
					fileSeleccionado.renameTo(faux);
					ficherosRaiz= construyeArbol(raizSD);	
					adapterSD.notifyDataSetChanged();
				}
			}
		});

		
		alert.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() 
		{
		  public void onClick(DialogInterface dialog, int whichButton) 
		  {
		   
		  }
		});

		alert.show();
		
		
	}
	
	public OnClickListener getListener()
	{
		return new OnClickListener()
		{
			@Override
			public void onClick(View v) 
			{
				crearCarpeta();
				
			}
		};
	}
	
	protected Dialog onCreateDialog(int id) 
	{
	    Dialog dialogo = null;
	 
	    switch(id)
	    {
	        case DIALOGO_OPCIONES:
	            dialogo = crearDialogoOpciones();
	            break;
	        //...
	        default:
	            dialogo = null;
	            break;
	    }
	 
	    return dialogo; 
	}
	
	private Dialog crearDialogoOpciones() 
	{
		final String[] items = 
		{
				getResources().getString(R.string.editFolder),
				getResources().getString(R.string.newFolder),
				getResources().getString(R.string.deleteFolder)
		};
		 
	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
	 
	   
	    builder.setItems(items,new DialogInterface.OnClickListener() 
	    {
	        public void onClick(DialogInterface dialog, int item)
	        {
	            switch(item) 
	            {
		            case 0: //editar nombre						
						renombrarCarpeta();
		                break;
		            case 1: 						
						// crear carpeta
						crearCarpeta();
		                break;
		            case 2: 						
						// borrar carpeta
		            	borrarCarpeta();
						
		                break;
	            }
	        }
	    });
	    return builder.create();
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
				view=convertView;
				holder = (ViewHolder) convertView.getTag();
			}
			
					
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
			
			view.setOnLongClickListener(new OnLongClickListener() 
			{
				
				@SuppressWarnings("deprecation")
				@Override
				public boolean onLongClick(View v) 
				{
					fileSeleccionado=ficheroActual;
					showDialog(DIALOGO_OPCIONES);
					return true;
				}
			});
			
			return view;
		}
		
	}


}
