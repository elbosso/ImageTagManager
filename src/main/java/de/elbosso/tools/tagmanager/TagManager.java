/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package de.elbosso.tools.tagmanager;

import bsh.DelayedEvalBshMethod;
import de.elbosso.ui.image.ImageDescription;
import de.netsysit.ui.components.ImageGallery;
import de.netsysit.util.ResourceLoader;
import de.netsysit.util.pattern.command.FileProcessor;
import org.slf4j.event.Level;

import javax.swing.*;
import javax.swing.plaf.ButtonUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.beans.DefaultPersistenceDelegate;
import java.beans.PropertyChangeEvent;
import java.beans.XMLEncoder;
import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;

/**
 *
 * @author elbosso
 */
public class TagManager extends Object implements ImageGallery.EventCallback
	,java.beans.PropertyChangeListener
	, de.elbosso.ui.components.TagManager.Listener
	,java.awt.event.WindowListener
	, ImageGallery.ImageGalleryListener
{
	private final static org.slf4j.Logger CLASS_LOGGER = org.slf4j.LoggerFactory.getLogger(TagManager.class);
	private final java.io.File applicationConfigFile=new java.io.File(de.elbosso.util.Utilities.getConfigDirectory(TagManager.class.getName()),"applicationConfiguration.xml");
	public static void main(String[] args) throws IOException
	{
		try
		{
			java.util.Properties iconFallbacks = new java.util.Properties();
			java.io.InputStream is=de.netsysit.util.ResourceLoader.getResource("de/elbosso/ressources/data/icon_trans_material.properties").openStream();
			iconFallbacks.load(is);
			is.close();

			iconFallbacks.put("de/elbosso/ressources/gfx/eb/function keys/f01_48.png","eb/svg/design/bitmap/function keys/f01_32.png");
			iconFallbacks.put("de/elbosso/ressources/gfx/eb/function keys/f02_48.png","eb/svg/design/bitmap/function keys/f02_32.png");
			iconFallbacks.put("de/elbosso/ressources/gfx/eb/function keys/f03_48.png","eb/svg/design/bitmap/function keys/f03_32.png");
			iconFallbacks.put("de/elbosso/ressources/gfx/eb/function keys/f04_48.png","eb/svg/design/bitmap/function keys/f04_32.png");
			iconFallbacks.put("de/elbosso/ressources/gfx/eb/function keys/f05_48.png","eb/svg/design/bitmap/function keys/f05_32.png");
			iconFallbacks.put("de/elbosso/ressources/gfx/eb/function keys/f06_48.png","eb/svg/design/bitmap/function keys/f06_32.png");
			iconFallbacks.put("de/elbosso/ressources/gfx/eb/function keys/f07_48.png","eb/svg/design/bitmap/function keys/f07_32.png");
			iconFallbacks.put("de/elbosso/ressources/gfx/eb/function keys/f08_48.png","eb/svg/design/bitmap/function keys/f08_32.png");
			iconFallbacks.put("de/elbosso/ressources/gfx/eb/function keys/f09_48.png","eb/svg/design/bitmap/function keys/f09_32.png");
			iconFallbacks.put("de/elbosso/ressources/gfx/eb/function keys/f10_48.png","eb/svg/design/bitmap/function keys/f10_32.png");
			iconFallbacks.put("de/elbosso/ressources/gfx/eb/function keys/f11_48.png","eb/svg/design/bitmap/function keys/f11_32.png");
			iconFallbacks.put("de/elbosso/ressources/gfx/eb/function keys/f12_48.png","eb/svg/design/bitmap/function keys/f12_32.png");
			iconFallbacks.put("de/netsysit/ressources/gfx/ca/öffnen_48.png","file/drawable-mdpi/ic_folder_open_black_48dp.png");
			iconFallbacks.put("de/netsysit/ressources/gfx/common/ViewPortNavigator24.gif","gfx/compass_48.png");
			de.netsysit.util.ResourceLoader.configure(iconFallbacks);
			de.netsysit.util.ResourceLoader.setSize(ResourceLoader.IconSize.medium);
		}
		catch(java.io.IOException ioexp)
		{
			ioexp.printStackTrace();
		}
		ResourceLoader.setSize(ResourceLoader.IconSize.small);
		de.elbosso.util.Utilities.configureBasicStdoutLogging(Level.TRACE);
		new TagManager();
	}
	private JScrollPane scroller;
	private de.elbosso.ui.components.TagManager tagManager;
	private de.elbosso.ui.image.ImageViewer imageViewer;
	private ImageGallery imggal;
	private Action showNextImageAction;
	private Action showPreviousImageAction;
	private Action gotoParentFolderAction;
	private Action mergeSelectedTagsOntoAllImagesInFolderAction;
	private javax.swing.Action clearTagsAction;
	private de.netsysit.util.pattern.command.ChooseFileAction addOntologyAction;
	private de.netsysit.util.pattern.command.ChooseFileAction exportOntologyAction;
	private javax.swing.Action gotoNextUntaggedAction;
	private int selectedImageIndex=-1;
	private final TagRepository tagRepository;
	private ApplicationConfiguration applicationConfiguration=new ApplicationConfiguration();
	private JPanel tagmanagement;

	public TagManager()
	{
		super();
		try
		{
			java.io.FileInputStream fis = new java.io.FileInputStream(applicationConfigFile);
			java.beans.XMLDecoder dec = new java.beans.XMLDecoder(fis);
			ApplicationConfiguration ac = (ApplicationConfiguration) dec.readObject();
			dec.close();
			fis.close();
			applicationConfiguration = ac;
		}
		catch(java.lang.Throwable t)
		{
			CLASS_LOGGER.warn(t.getMessage(),t);
		}
		tagRepository=new PropertyTagRepositoryImpl();
		JFrame f=new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.addWindowListener(this);
		f.addWindowListener(this);
		createActions();
		File root=null;
		if(System.getProperty("de.elbosso.scratch.ui.ImageGallery.ontology")!=null)
			root=new File(System.getProperty("de.elbosso.scratch.ui.ImageGallery.ontology"));
		else
		{
			root = new java.io.File(de.elbosso.util.Utilities.getConfigDirectory(this.getClass().getName()),"ontology.conf");
			CLASS_LOGGER.debug("XDG config directory: " + root);
			if ((root == null)&&((root.exists())&&(root.isFile())))
				root = new File(System.getProperty("user.home"));
		}
		java.util.List<String> l=new java.util.LinkedList(initCategories(root));
		if((applicationConfiguration.getOntology()!=null)&&(applicationConfiguration.getOntology().isEmpty()==false))
		{
			l = new java.util.LinkedList();
			for (de.elbosso.util.lang.TagDescription tagDescription : applicationConfiguration.getOntology())
				l.add(tagDescription.getName());
		}
		tagManager=new de.elbosso.ui.components.TagManager(l,applicationConfiguration.getFavourites());
		tagManager.addListener(this);
		tagmanagement=new JPanel(new BorderLayout());
		JPanel toplevel=new JPanel(new BorderLayout());
		toplevel.add(tagmanagement);
		de.elbosso.ui.components.container.PalettePanel palette=new de.elbosso.ui.components.container.PalettePanel();
		palette.add(clearTagsAction);
		palette.add(addOntologyAction);
		palette.add(exportOntologyAction);
		palette.add(gotoNextUntaggedAction);
		scroller=new JScrollPane(tagManager.getAllTagsPanel());
		scroller.setPreferredSize(new Dimension(420,400));
		//scroller.setWheelScrollingEnabled(false);
		scroller.getVerticalScrollBar().setUnitIncrement(20);
		tagmanagement.add(tagManager.getTagTexField(), BorderLayout.SOUTH);
		registerCustomKeyStrokes();
		scroller.setPreferredSize(new Dimension(420,400));
		JTree tree=new JTree(tagManager.getTreeModel());
		tree.setFont(tree.getFont().deriveFont(tree.getFont().getSize2D()/1.1f));
		JScrollPane treescroller=new JScrollPane(tree);
//		toplevel.add(scroller, BorderLayout.WEST);
		tagmanagement.add(tagManager.getSelectedTagsPanel()/*selscroller*/, BorderLayout.NORTH);
		tagmanagement.add(treescroller, BorderLayout.EAST);
		root=applicationConfiguration.getCurrentDir();
		tagRepository.setDirectory(root);
		imggal=new ImageGallery(root,150,false);
		imggal.setEventCallback(TagManager.this);
		imggal.addImageGalleryListener(this);
		imggal.addPropertyChangeListener(this);
		imageViewer=new de.elbosso.ui.image.ImageViewer();
		JPanel imggalp=new JPanel(new BorderLayout());
		imggalp.add(imggal);
		imggalp.add(setupGalleryToolbar(), BorderLayout.NORTH);
		JSplitPane split=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, imggalp, imageViewer);
		de.netsysit.ui.components.DockingPanel dockingPanel=new de.netsysit.ui.components.DockingPanel(split, SwingConstants.WEST);
		dockingPanel.addDockable(scroller,"Tags");
		JScrollPane favsScroller=new JScrollPane(tagManager.getFavsList());
		de.elbosso.ui.renderer.list.IconProviderRenderer renderer=new de.elbosso.ui.renderer.list.IconProviderRenderer()
		{
			@Override
			protected Icon supplyIcon(JList list,
									  Object value,
									  int index,
									  boolean sel,
									  boolean hasFocus)
			{
				Icon icon=null;
				int i=index+1;
				if(index<12)
				{
					if(i<10)
						icon=ResourceLoader.getIcon("de/elbosso/ressources/gfx/eb/function keys/f0"+i+"_48.png");
					else
						icon=ResourceLoader.getIcon("de/elbosso/ressources/gfx/eb/function keys/f"+i+"_48.png");
				}
				return icon;
			}
		};
		renderer.setDefaultIcon(new ImageIcon(ResourceLoader.getImgResource("de/elbosso/ressources/gfx/common/Empty_48.png")));
		tagManager.getFavsList().setCellRenderer(renderer);
		dockingPanel.addDockable(favsScroller,"Favourites");
		dockingPanel.addDockable(palette,"Palette");
		tagmanagement.add(dockingPanel);
		f.setContentPane(toplevel);
		f.pack();
		f.setVisible(true);
		tagManager.getTagTexField().requestFocusInWindow();
		selectedImageIndex=imggal.getSelectedIndex();
	}

	private JToolBar setupGalleryToolbar()
	{
		JToolBar imggaltb=new JToolBar();
		imggaltb.setFloatable(false);
		imggaltb.add(showPreviousImageAction);
		imggaltb.add(showNextImageAction);
		imggaltb.addSeparator();
		imggaltb.add(gotoParentFolderAction);
		imggaltb.add(mergeSelectedTagsOntoAllImagesInFolderAction);
		return imggaltb;
	}

	private void registerCustomKeyStrokes()
	{
		String keyStrokeAndKey = "alt ENTER";
		KeyStroke keyStroke = KeyStroke.getKeyStroke(keyStrokeAndKey);//KeyEvent.VK_BACK_SPACE,0);
		CLASS_LOGGER.debug(java.util.Objects.toString(keyStroke));
		tagManager.getTagTexField().getInputMap().put(keyStroke, keyStrokeAndKey);
		tagManager.getTagTexField().getActionMap().put(keyStrokeAndKey, showNextImageAction);
		keyStrokeAndKey = "alt shift ENTER";
		keyStroke = KeyStroke.getKeyStroke(keyStrokeAndKey);//KeyEvent.VK_BACK_SPACE,0);
		CLASS_LOGGER.debug(java.util.Objects.toString(keyStroke));
		tagManager.getTagTexField().getInputMap().put(keyStroke, keyStrokeAndKey);
		tagManager.getTagTexField().getActionMap().put(keyStrokeAndKey, showPreviousImageAction);
		keyStrokeAndKey = "alt UP";
		keyStroke = KeyStroke.getKeyStroke(keyStrokeAndKey);//KeyEvent.VK_BACK_SPACE,0);
		CLASS_LOGGER.debug(java.util.Objects.toString(keyStroke));
		tagManager.getTagTexField().getInputMap().put(keyStroke, keyStrokeAndKey);
		tagManager.getTagTexField().getActionMap().put(keyStrokeAndKey, gotoParentFolderAction);
	}

	private void createActions()
	{
		showNextImageAction=new AbstractAction(null, ResourceLoader.getIcon("de/netsysit/ressources/gfx/common/Next24.gif"))
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(selectedImageIndex<imggal.getImageCount()-1)
					showNextImage();
			}
		};
		showPreviousImageAction=new AbstractAction(null, ResourceLoader.getIcon("de/netsysit/ressources/gfx/common/Previous24.gif"))
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(selectedImageIndex>0)
					showPreviousImage();
			}
		};
		gotoParentFolderAction=new AbstractAction(null, ResourceLoader.getIcon("de/elbosso/ressources/gfx/eb/folder_parent_48.png"))
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				directoryChangeImminent(imggal.getRoot(),imggal.getRoot().getParentFile());
				imggal.setRoot(imggal.getRoot().getParentFile());
			}
		};
		mergeSelectedTagsOntoAllImagesInFolderAction=new AbstractAction(null, ResourceLoader.getIcon("de/elbosso/ressources/gfx/eb/tag_multiple_48.png"))
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				mergeSelectedTagsOntoAllImagesInFolder();
			}
		};
		mergeSelectedTagsOntoAllImagesInFolderAction.setEnabled(false);
		clearTagsAction=new javax.swing.AbstractAction(null,de.netsysit.util.ResourceLoader.getIcon("toolbarButtonGraphics/general/Remove24.gif"))
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				tagManager.clearTags();
				tagmanagement.invalidate();
				tagmanagement.validate();
				tagmanagement.doLayout();
				tagmanagement.repaint();
			}
		};
		de.netsysit.util.pattern.command.FileProcessor addOntology=new FileProcessor()
		{
			@Override
			public boolean process(File[] files)
			{
				boolean rv=false;
				java.util.Collection<java.lang.String> coll=new java.util.LinkedList();
				for(java.io.File file:files)
				{
					coll.addAll(initCategories(file));
				}
				if(coll.isEmpty()==false)
				{
					for (java.lang.String tagName : coll)
						tagManager.addTag(tagName);
					tagmanagement.invalidate();
					tagmanagement.validate();
					tagmanagement.doLayout();
					tagmanagement.repaint();
				}
				rv=true;
				return rv;
			}
		};
		addOntologyAction=new de.netsysit.util.pattern.command.ChooseFileAction(addOntology,null,de.netsysit.util.ResourceLoader.getIcon("toolbarButtonGraphics/general/Add24.gif"));
		de.netsysit.util.pattern.command.FileProcessor exportOntology=new FileProcessor()
		{
			@Override
			public boolean process(File[] files)
			{
				boolean rv=false;
				try
				{
					java.io.FileOutputStream fos = new java.io.FileOutputStream(files[0]);
					java.io.PrintWriter pw = new java.io.PrintWriter(fos);
					javax.swing.ListModel<de.elbosso.util.lang.TagDescription> tagDescriptionListModel=tagManager.getListModel();
					for(int i=0;i<tagDescriptionListModel.getSize();++i)
						pw.println(tagDescriptionListModel.getElementAt(i).getName());
					pw.close();
					fos.close();
					rv = true;
				}
				catch(java.io.IOException exp)
				{
					de.elbosso.util.Utilities.handleException(CLASS_LOGGER,exp);
				}
				return rv;
			}
		};
		exportOntologyAction=new de.netsysit.util.pattern.command.ChooseFileAction(exportOntology,null,de.netsysit.util.ResourceLoader.getIcon("toolbarButtonGraphics/general/Export24.gif"));
		exportOntologyAction.setSaveDialog(true);
		gotoNextUntaggedAction=new javax.swing.AbstractAction(null,de.netsysit.util.ResourceLoader.getIcon("de/netsysit/ressources/gfx/common/HighlightSelection24.gif"))
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				int currentIndex=0;
				int firstUntaggedIndex=-1;
				collectTags();
				try
				{
					while(true)
					{
						ImageDescription selectedImageDescription = imggal.getImageDescription(currentIndex);
						if(selectedImageDescription.isFolder()==false)
						{
							if((selectedImageDescription.getTags()==null)||(selectedImageDescription.getTags().isEmpty()))
								tagRepository.readTags(selectedImageDescription);
							if(removeAutomaticTags(selectedImageDescription.getTags()).isEmpty()==true)
							{
								firstUntaggedIndex=currentIndex;
								break;
							}
						}
						++currentIndex;
					}
				}
				catch(java.lang.Throwable t)
				{

				}
				if(firstUntaggedIndex>-1)
				{
					imggal.setSelectedIndex(firstUntaggedIndex);
					imageViewer.setImage(imggal.getImageDescription(firstUntaggedIndex).getImgData());
					selectedImageIndex = firstUntaggedIndex;
					if(selectedImageIndex>-1)
						startTaggingWorkOnImage(imggal.getImageDescription(selectedImageIndex));
					CLASS_LOGGER.debug(imggal.getSelectedIndex()+" "+imggal.getImageCount());
					showPreviousImageAction.setEnabled(imggal.getSelectedIndex()>0);
					showNextImageAction.setEnabled(imggal.getSelectedIndex()<imggal.getImageCount()-1);
					tagManager.getTagTexField().requestFocusInWindow();
				}

			}
		};
	}

	private void showNextImage()
	{
		collectTags();
		int currentSelection=imggal.getSelectedIndex();
//		System.out.println(currentSelection+" "+imggal.getImageCount());
		while(currentSelection<imggal.getImageCount()-1)
		{
//			System.out.println(currentSelection+" "+imggal.getImageCount());
			++currentSelection;
			if(imggal.getImageDescription(currentSelection).isFolder()==false)
			{
				imggal.setSelectedIndex(currentSelection);
				try
				{
					imageViewer.setImage(imggal.getImageDescription(currentSelection).getImgData());
					selectedImageIndex = currentSelection;

//				System.out.println(imggal.getSelectedIndex());
					break;
				}
				catch(NullPointerException exp)
				{
					CLASS_LOGGER.warn(java.util.Objects.toString(imageViewer));
					CLASS_LOGGER.warn(java.util.Objects.toString(imggal));
					CLASS_LOGGER.warn(java.util.Objects.toString(imggal.getImageDescription(currentSelection)));
					CLASS_LOGGER.warn(java.util.Objects.toString(imggal.getImageDescription(currentSelection).getImgData()));
				}
			}
			else
			{
				imggal.setSelectedIndex(-1);
				selectedImageIndex=-1;
				imggal.setRoot(imggal.getImageDescription(currentSelection).getFolder());
				break;
			}
		}
		if(selectedImageIndex>-1)
			startTaggingWorkOnImage(imggal.getImageDescription(selectedImageIndex));
		CLASS_LOGGER.debug(imggal.getSelectedIndex()+" "+imggal.getImageCount());
		showPreviousImageAction.setEnabled(imggal.getSelectedIndex()>0);
		showNextImageAction.setEnabled(imggal.getSelectedIndex()<imggal.getImageCount()-1);
		tagManager.getTagTexField().requestFocusInWindow();
	}
	private void showPreviousImage()
	{
		collectTags();
		int currentSelection=imggal.getSelectedIndex();
		while(currentSelection>0)
		{
			--currentSelection;
			if(imggal.getImageDescription(currentSelection).isFolder()==false)
			{
				imggal.setSelectedIndex(currentSelection);
				imageViewer.setImage(imggal.getImageDescription(currentSelection).getImgData());
				selectedImageIndex=currentSelection;
				break;
			}
			else
			{
				imggal.setSelectedIndex(-1);
				selectedImageIndex=-1;
				imggal.setRoot(imggal.getImageDescription(currentSelection).getFolder());
				break;
			}
		}
		if(selectedImageIndex>-1)
			startTaggingWorkOnImage(imggal.getImageDescription(selectedImageIndex));
		showPreviousImageAction.setEnabled(imggal.getSelectedIndex()>0);
		showNextImageAction.setEnabled(imggal.getSelectedIndex()<imggal.getImageCount()-1);
		tagManager.getTagTexField().requestFocusInWindow();
	}
	private java.util.Collection<String> initCategories(java.io.File root)
	{
/*		categories.add("car|motorcycle|truck.color.black");
		categories.add("car|motorcycle|truck.color.blue");
		categories.add("car|motorcycle|truck.color.green");
		categories.add("car|motorcycle|truck.color.red");
		categories.add("car|motorcycle|truck.color.yellow");
		categories.add("car|motorcycle|truck.color.white");
		categories.add("truck.wheels.4");
		categories.add("truck.wheels.6");
		categories.add("truck.wheels.8");
		categories.add("car.style.convertible");
		categories.add("car.style.limousine");
		categories.add("car.style.van");
		categories.add("car.seats.2");
		categories.add("car.seats.4");
		categories.add("car.seats.5");
		categories.add("car.seats.7");
		categories.add("car|motorcycle.tuneup.standard");
		categories.add("car|motorcycle.tuneup.custom");*/
//		categories.add("");
//		categories.add("");
//		categories.add("");

		java.util.List<String> refined=new java.util.LinkedList();
		try
		{
			java.io.InputStream is=new java.io.FileInputStream(root);

			String[] categories=de.elbosso.util.Utilities.readIntoStringArray(is);
			for(String category:categories)
			{
				if(category.trim().length()>0)
				{
					java.util.List<String> ls = new java.util.LinkedList();
					String[] hierarchies = category.split("\\.");
					for (String hierarchy : hierarchies)
					{
						String[] parts = hierarchy.split("\\|");
						java.util.List<String> intermediate = new java.util.LinkedList();
						for (String part : parts)
						{
							if (ls.isEmpty())
							{
								intermediate.add(part);
							}
							else
							{
								for (String l : ls)
								{
									intermediate.add(l + "." + part);
								}
							}
						}
						ls = intermediate;
					}
					refined.addAll(ls);
				}
			}

		}
		catch(java.io.IOException exp)
		{
		}
		catch(java.lang.NullPointerException exp)
		{
		}
		return refined;
	}

	private void startTaggingWorkOnImage(ImageDescription imageDescription)
	{
		tagManager.deselectAll();
		if(imageDescription!=null)
		{
			tagRepository.readTags(imageDescription);
			CLASS_LOGGER.debug("Starting work on "+imageDescription+" with tags: "+imageDescription.getTags());
			tagManager.setSelectedTagsAsString(removeAutomaticTags(imageDescription.getTags()));
		}
	}

	private String removeAutomaticTags(String tags)
	{
		String[] parts=tags.split(",");
		StringBuffer buf=new StringBuffer();
		for(String part:parts)
		{
			if(part.startsWith("_")==false)
			{
				if (buf.length() > 0)
					buf.append(",");
				buf.append(part);
			}
		}
		return buf.toString();
	}

	private String makeAutoTags(ImageDescription selectedImageDescription)
	{
		StringBuffer autoTags=new StringBuffer();
		autoTags.append("_image.width.");
		autoTags.append(selectedImageDescription.getWidth());
		autoTags.append(",_image.height.");
		autoTags.append(selectedImageDescription.getHeight());
		autoTags.append(",_image.aspect.");
		autoTags.append(selectedImageDescription.getWidth()>selectedImageDescription.getHeight()?"landscape":"portrait");
		try
		{
			String mimeType=selectedImageDescription.getMimeType();
			autoTags.append(",_image.mimeType.");
			autoTags.append(mimeType);
		}
		catch(java.io.IOException ioexp)
		{
			CLASS_LOGGER.error(ioexp.getMessage(),ioexp);
		}
		return autoTags.toString();
	}
	private void collectTags()
	{
		if(selectedImageIndex>-1)
		{
			ImageDescription selectedImageDescription=imggal.getImageDescription(selectedImageIndex);
			StringBuffer autoTags=new StringBuffer(makeAutoTags(selectedImageDescription));
			String tags=tagManager.getSelectedTagsAsString();
			if((tags!=null)&&(tags.trim().length()>1))
			{
				autoTags.append(",");
				autoTags.append(tags);
			}
			selectedImageDescription.setTags(autoTags.toString());
			tagRepository.writeTags(selectedImageDescription);
		}
	}
	private String mergeTags(String oldTags, String newTags)
	{
		java.util.Set<String> tags=new java.util.HashSet();
		if(oldTags!=null)
		{
			String[] parts = oldTags.split(",");
			if (parts != null)
			{
				for (String part : parts)
				{
					if(part.startsWith("_")==false)
						tags.add(part);
				}
			}
		}
		if(newTags!=null)
		{
			String[] parts = newTags.split(",");
			if(parts!=null)
			{
				java.util.List<String> l = java.util.Arrays.asList(parts);
				tags.addAll(l);
			}
		}
		StringBuffer buf=new StringBuffer();
		for(String tag:tags)
		{
			if(buf.length()>1)
				buf.append(",");
			buf.append(tag);
		}
		return buf.toString();
	}
	private void mergeSelectedTagsOntoAllImagesInFolder()
	{
		if(selectedImageIndex>-1)
		{
			collectTags();
			ImageDescription selectedImageDescription = imggal.getImageDescription(selectedImageIndex);
			String newTags=selectedImageDescription.getTags();
			for (int i = 0; i < imggal.getImageCount();++i)
			{
				ImageDescription imageDescription = imggal.getImageDescription(i);
				if(imageDescription.isFolder()==false)
				{
					if (i != selectedImageIndex)
					{
						tagRepository.readTags(imageDescription);
						String oldTags=imageDescription.getTags();
						StringBuffer autoTags=new StringBuffer(makeAutoTags(imageDescription));
						String mergedTags=mergeTags(oldTags,newTags);
						if((mergedTags!=null)&&(mergedTags.trim().length()>1))
						{
							autoTags.append(",");
							autoTags.append(mergedTags);
						}
						CLASS_LOGGER.debug("setting tags "+autoTags.toString()+" for "+imageDescription);
						imageDescription.setTags(autoTags.toString());
						tagRepository.writeTags(imageDescription);
					}
				}
			}
		}
	}

	@Override
	public void singleClick(ImageDescription imageDescription)
	{

	}

	@Override
	public void doubleClick(ImageDescription imageDescription)
	{
		collectTags();
		if(imageDescription.isFolder()==false)
		{
			selectedImageIndex=imggal.indexOf(imageDescription);
			imageViewer.setImage(imageDescription.getImgData());
			startTaggingWorkOnImage(imageDescription);
		}
		else
			selectedImageIndex=-1;
		CLASS_LOGGER.debug(java.lang.Integer.toString(selectedImageIndex));
		tagManager.getTagTexField().requestFocusInWindow();
	}

	@Override
	public void listSelectionChanged()
	{
		showPreviousImageAction.setEnabled(imggal.getSelectedIndex()>0);
		showNextImageAction.setEnabled(imggal.getSelectedIndex()<imggal.getImageCount()-1);
		if(CLASS_LOGGER.isDebugEnabled())CLASS_LOGGER.debug("listSelectionChanged");
		java.util.List<Integer> selectedIndices=imggal.getSelectedIndices();
		for(Integer index:selectedIndices)
		{
			if((index>-1)&&(index<imggal.getImageCount()))
				if(CLASS_LOGGER.isDebugEnabled())CLASS_LOGGER.debug("selected"+(imggal.getImageDescription(index).isFolder()?" folder ":" ")+"index: "+index+"; "+(imggal.getImageDescription(index).isFolder()?imggal.getImageDescription(index).getFolder():imggal.getImageDescription(index).getUri()));
		}
	}

	@Override
	public void directoryChangeImminent(File directorytoBeLeft, File newRoot)
	{
		CLASS_LOGGER.debug("Leaving directory "+directorytoBeLeft+" for "+newRoot);
		collectTags();
		selectedImageIndex=-1;
		tagRepository.setDirectory(newRoot);
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt)
	{
		if(evt.getPropertyName().equals("imageCount"))
		{
			CLASS_LOGGER.debug("imageCount: "+evt.getNewValue());
			showPreviousImageAction.setEnabled(imggal.getSelectedIndex()>0);
			showNextImageAction.setEnabled(imggal.getSelectedIndex()<imggal.getImageCount()-1);
//			System.out.println(imggal.getSelectedIndex()+" "+(imggal.getImageCount()));
//			if((imggal.getSelectedIndex()<0)&&(imggal.getImageCount()>0))
//				showNextImage();
			showPreviousImageAction.setEnabled(imggal.getSelectedIndex()>0);
			showNextImageAction.setEnabled(imggal.getSelectedIndex()<imggal.getImageCount()-1);
		}
		else if(evt.getPropertyName().equals("selectedTagsAsString"))
		{
			CLASS_LOGGER.debug("currently selected: "+evt.getNewValue());
		}
	}

	@Override
	public void tagAdded(String name)
	{
		CLASS_LOGGER.debug("added: "+name);
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				tagManager.getTagTexField().setText("");
				tagManager.getTagTexField().requestFocusInWindow();
			}
		});
	}

	@Override
	public void tagRemoved(String name)
	{
		CLASS_LOGGER.debug("removed: "+name);
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				//tagManager.getTagTexField().setText("");
				tagManager.getTagTexField().requestFocusInWindow();
			}
		});
	}

	@Override
	public void tagAddedViaGui(String name)
	{
		CLASS_LOGGER.debug("tagAddedViaGui "+name);
	}

	@Override
	public void windowOpened(WindowEvent e)
	{

	}

	@Override
	public void windowClosing(WindowEvent e)
	{
		CLASS_LOGGER.debug("windowClosing");
		collectTags();
		applicationConfiguration.setCurrentDir(tagRepository.getDirectory());
		CLASS_LOGGER.error(tagRepository.getDirectory()+" "+applicationConfiguration.getCurrentDir());
		applicationConfiguration.setOntology(new java.util.ArrayList(de.elbosso.model.Utilities.asList(de.elbosso.util.lang.TagDescription.class, tagManager.getListModel())));
		applicationConfiguration.setFavourites(tagManager.getTagFavs());
		try
		{
			java.io.FileOutputStream fos = new java.io.FileOutputStream(applicationConfigFile);
			java.beans.XMLEncoder enc = new XMLEncoder(fos);
			enc=de.elbosso.util.io.PersistenceDelegateRegistry.getSharedInstance().configureFor(java.io.File.class,enc);
			enc.writeObject(applicationConfiguration);
			enc.close();
			fos.close();
		}
		catch(java.io.IOException exp)
		{
			CLASS_LOGGER.error(exp.getMessage(),exp);
		}

	}

	@Override
	public void windowClosed(WindowEvent e)
	{
		CLASS_LOGGER.debug("windowClosed");
	}

	@Override
	public void windowIconified(WindowEvent e)
	{

	}

	@Override
	public void windowDeiconified(WindowEvent e)
	{

	}

	@Override
	public void windowActivated(WindowEvent e)
	{

	}

	@Override
	public void windowDeactivated(WindowEvent e)
	{
		CLASS_LOGGER.debug("windowDeactivated");
		collectTags();
	}

	@Override
	public void directoryReadingStarted()
	{
		mergeSelectedTagsOntoAllImagesInFolderAction.setEnabled(false);
	}

	@Override
	public void directoryReadingEnded()
	{
		CLASS_LOGGER.debug("directoryReadingEnded");
		mergeSelectedTagsOntoAllImagesInFolderAction.setEnabled(true);
	}

	public interface TagRepository
	{
		void readTags(ImageDescription imageDescription);
		void writeTags(ImageDescription imageDescription);
		File getDirectory();
		void setDirectory(File directory);
	}

	class PropertyTagRepositoryImpl extends Object implements TagRepository
	{
		private java.util.Properties props=new java.util.Properties();
		private File directory;

		@Override
		public void readTags(ImageDescription imageDescription)
		{
			if(props.containsKey(imageDescription.toString()))
				imageDescription.setTags(props.getProperty(imageDescription.toString()));
/*			else
			{
				java.util.Random rand = new java.util.Random(System.currentTimeMillis());
				java.lang.StringBuffer buf = new java.lang.StringBuffer();
				for (int i = 0; i < 4; ++i)
				{
					if (i > 0)
						buf.append(",");
					buf.append(tagManager.getListModel().getElementAt(rand.nextInt(tagManager.getListModel().getSize())));
				}
				imageDescription.setTags(buf.toString());
			}
*/		}
		@Override
		public void writeTags(ImageDescription imageDescription)
		{
			if(imageDescription!=null)
			{
				CLASS_LOGGER.debug("Writing tags " + imageDescription.getTags() + " for " + imageDescription);
				props.setProperty(imageDescription.toString(),imageDescription.getTags());
				File tagsFile=new File(directory,".tagManager.properties");
				CLASS_LOGGER.debug("Storing to "+tagsFile);
				try(java.io.FileOutputStream fos=new java.io.FileOutputStream(tagsFile))
				{
					props.store(fos,"Tags written by "+(TagManager.class.getName())+" on "+new java.util.Date());
				}
				catch(java.io.IOException ioexp)
				{
					CLASS_LOGGER.warn(ioexp.getMessage(),ioexp);
				}
			}
		}

		public File getDirectory()
		{
			return directory;
		}

		public void setDirectory(File directory)
		{
			CLASS_LOGGER.debug("Working in "+directory);
			props.clear();
			File old = getDirectory();
			this.directory = directory;
			File tagsFile=new File(directory,".tagManager.properties");
			if((tagsFile.exists())&&(tagsFile.isFile()))
			{
				CLASS_LOGGER.debug("Loading from "+tagsFile);
				try(java.io.FileInputStream fis=new java.io.FileInputStream(tagsFile))
				{
					props.load(fis);
				}
				catch(java.io.IOException ioexp)
				{
					CLASS_LOGGER.warn(ioexp.getMessage(),ioexp);
				}
			}
//			send("directory", old, getDirectory());
		}
	}
}
