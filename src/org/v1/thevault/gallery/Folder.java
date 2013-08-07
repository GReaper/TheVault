package org.v1.thevault.gallery;

import java.io.File;

public class Folder implements Comparable<Folder>
{
	private File carpeta;
	private File imagen;
	
	public Folder(File carpeta)
	{
		this.carpeta=carpeta;
	}

	public File getCarpeta()
	{
		return carpeta;
	}

	public void setCarpeta(File carpeta)
	{
		this.carpeta = carpeta;
	}

	public File getImagen() 
	{
		return imagen;
	}

	public void setImagen(File imagen)
	{
		this.imagen = imagen;
	}

	@Override
	public int compareTo(Folder another) 
	{
		return this.getCarpeta().getName().compareTo(another.getCarpeta().getName());
	}
}
