package de.elbosso.tools.tagmanager;

import de.elbosso.util.lang.TagDescription;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class ApplicationConfiguration extends de.elbosso.util.beans.EventHandlingSupport
{
 	private final static org.slf4j.Logger CLASS_LOGGER = org.slf4j.LoggerFactory.getLogger(ApplicationConfiguration.class);
    private java.io.File currentDir=new File(System.getProperty("de.elbosso.scratch.ui.ImageGallery.folder"),"/home/elbosso");
    private java.util.List<de.elbosso.util.lang.TagDescription> ontology;
    private java.util.Map<java.lang.String, AtomicLong> favourites;

    public ApplicationConfiguration()
    {
        super();
        if(System.getProperty("de.elbosso.scratch.ui.ImageGallery.folder")!=null)
            currentDir=new File(System.getProperty("de.elbosso.scratch.ui.ImageGallery.folder"));
        else
        {
            currentDir = de.elbosso.util.Utilities.getPicturesDirectory();
            CLASS_LOGGER.debug("XDG pictures directory: " + currentDir);
            if ((currentDir == null)&&((currentDir.exists())&&(currentDir.isDirectory())))
                currentDir = new File(System.getProperty("user.home"));
        }
    }

    public File getCurrentDir()
    {
        return currentDir;
    }

    public void setCurrentDir(File currentDir)
    {
        File old = getCurrentDir();
        this.currentDir = new java.io.File(currentDir.toURI());
        send("currentDir", old, getCurrentDir());
    }

    public List<TagDescription> getOntology()
    {
        return ontology;
    }

    public void setOntology(List<TagDescription> ontology)
    {
        List<TagDescription> old = getOntology();
        this.ontology = ontology;
        send("ontology", old, getOntology());
    }

    public Map<String, AtomicLong> getFavourites()
    {
        return favourites;
    }

    public void setFavourites(Map<String, AtomicLong> favourites)
    {
        Map<String, AtomicLong> old = getFavourites();
        this.favourites = favourites;
        send("favourites", old, getFavourites());
    }
}
