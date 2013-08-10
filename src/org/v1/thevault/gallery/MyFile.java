package org.v1.thevault.gallery;

import java.io.File;

public class MyFile 
{
	private File file;
	private boolean seleccionado;
	
	public MyFile(File file)
	{
		this.file=file;
		this.seleccionado=false;
	}

	public File getFile()
	{
		return file;
	}

	public void setFile(File file) 
	{
		this.file = file;
	}

	public boolean isSeleccionado()
	{
		return seleccionado;
	}

	public void setSeleccionado(boolean seleccionado) 
	{
		this.seleccionado = seleccionado;
	}

	public void cambiarSeleccion() 
	{
		this.seleccionado=!this.seleccionado;		
	}
	
	
	

}
