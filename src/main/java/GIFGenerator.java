import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;
import java.util.ArrayList;
import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.*;

/**
 * GIF Generator for creating animated proof of execution
 * Creates GIFs from screenshot sequences with configurable timing
 */
public class GIFGenerator {
    
    private int frameDelay = 1000;
    private boolean loop = true;
    
    public GIFGenerator() {}
    
    public GIFGenerator(int frameDelay, boolean loop) {
        this.frameDelay = frameDelay;
        this.loop = loop;
    }
    
    /**
     * Create GIF from list of screenshot files
     */
    public boolean createGIF(List<String> screenshotPaths, String outputPath) {
        try {
            List<BufferedImage> images = new ArrayList<>();
            for (String path : screenshotPaths) {
                BufferedImage img = ImageIO.read(new File(path));
                if (img != null) {
                    images.add(img);
                }
            }
            return createGIFFromImages(images, outputPath);
        } catch (Exception e) {
            System.err.println("Error creating GIF: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Create GIF from BufferedImage list
     */
    public boolean createGIFFromImages(List<BufferedImage> images, String outputPath) {
        if (images.isEmpty()) return false;
        
        try {
            ImageWriter writer = ImageIO.getImageWritersByFormatName("gif").next();
            ImageOutputStream output = ImageIO.createImageOutputStream(new File(outputPath));
            writer.setOutput(output);
            
            writer.prepareWriteSequence(null);
            
            for (BufferedImage image : images) {
                IIOMetadata metadata = writer.getDefaultImageMetadata(
                    new ImageTypeSpecifier(image), null);
                
                configureGIFMetadata(metadata);
                
                writer.writeToSequence(new IIOImage(image, null, metadata), null);
            }
            
            writer.endWriteSequence();
            output.close();
            writer.dispose();
            
            System.out.println("GIF created successfully: " + outputPath);
            return true;
            
        } catch (Exception e) {
            System.err.println("Error creating GIF: " + e.getMessage());
            return false;
        }
    }
    
    private void configureGIFMetadata(IIOMetadata metadata) throws Exception {
        String metaFormatName = metadata.getNativeMetadataFormatName();
        IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(metaFormatName);
        
        IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute("delayTime", String.valueOf(frameDelay / 10));
        graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");
        
        IIOMetadataNode appExtensionsNode = getNode(root, "ApplicationExtensions");
        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");
        
        int loopContinuously = loop ? 0 : 1;
        child.setUserObject(new byte[]{ 0x1, (byte) (loopContinuously & 0xFF), (byte) ((loopContinuously >> 8) & 0xFF)});
        appExtensionsNode.appendChild(child);
        
        metadata.setFromTree(metaFormatName, root);
    }
    
    private static IIOMetadataNode getNode(IIOMetadataNode rootNode, String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName) == 0) {
                return((IIOMetadataNode) rootNode.item(i));
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return(node);
    }
}
