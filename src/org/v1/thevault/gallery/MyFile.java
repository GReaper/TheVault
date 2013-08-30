package org.v1.thevault.gallery;

import java.io.File;

public class MyFile 
{
	private File file;
	private boolean seleccionado;
	private byte[] bytes;
	
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

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
	
	
	

}
