package com.wjf.Run;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.filechooser.FileSystemView;

/**
 * java截屏
 * 运行后将当前屏幕截取，并最大化显示。
 * 拖拽鼠标，选择自己需要的部分。
 * 按Esc键保存图片到桌面，并退出程序。
 * 点击右上角（没有可见的按钮），退出程序，不保存图片。
 *
 * @author JinCeon
 */
public class SnapshotTest {
    public static void main(String[] args) {
        // 全屏运行
        RectD rd = new RectD();
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        gd.setFullScreenWindow(rd);
    }
}
 
class RectD extends JFrame {
    private static final long serialVersionUID = 1L;
    int orgx, orgy, endx, endy;
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    BufferedImage image;
    BufferedImage tempImage;
    BufferedImage saveImage;
    Graphics g;
    JLabel infoArea, colorLabel;
    JPanel panel;


 
    @Override
    public void paint(Graphics g) {
        RescaleOp ro = new RescaleOp(0.8f, 0, null);
        tempImage = ro.filter(image, null);
        g.drawImage(tempImage, 0, 0, this);
    }
 
    public RectD() {
    	snapshot();
    	panel = new JPanel();
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        infoArea = new JLabel();
        infoArea.setOpaque(false);
        infoArea.setForeground(Color.black);
        infoArea.setFont(new Font("楷体" , Font.BOLD ,50 ));
        infoArea.setText("width:" + d.width + ",height:" + d.height);
        panel.add(infoArea);
        colorLabel = new JLabel();
        colorLabel.setOpaque(false);
        colorLabel.setForeground(Color.black);
        colorLabel.setFont(new Font("楷体" , Font.BOLD ,50 ));
        panel.add(colorLabel);
        this.getContentPane().add(panel);
        this.setUndecorated(true); 
        setVisible(true);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                orgx = e.getX();
                orgy = e.getY();
            }
            public void mouseClicked(MouseEvent e){
            	if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 && saveImage!= null){
                    saveToFile();
                    System.exit(0);
            	}
            	if(e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON3){
                    System.exit(0);
            	}
            }
        });
        this.addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                endx = e.getX();
                endy = e.getY();
                g = getGraphics();
                g.drawImage(tempImage, 0, 0, RectD.this);
                int x = Math.min(orgx, endx);
                int y = Math.min(orgy, endy);
                int width = Math.abs(endx - orgx)+1;
                int height = Math.abs(endy - orgy)+1;
                // 加上1，防止width或height为0
                g.setColor(Color.BLUE);
                g.drawRect(x-1, y-1, width+1, height+1);
                //减1，加1都是为了防止图片将矩形框覆盖掉
                saveImage = image.getSubimage(x, y, width, height);
                g.drawImage(saveImage, x, y, RectD.this);
                infoArea.setText("width:" + width + ",height:" + height);
            }
            public void mouseMoved(MouseEvent e){
            	colorLabel.setText("x:" + e.getXOnScreen() + ",y:" + e.getYOnScreen());
            }
        });
        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                // 按Esc键退出
                if (e.getKeyCode() == 27) {
                    saveToFile();
                    System.exit(0);
                }
            }
        });
    }
 
    public void saveToFile() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String name = sdf.format(new Date());
        File path = FileSystemView.getFileSystemView().getHomeDirectory();
        String format = "png";
        File f = new File(path + File.separator + name + "." + format);
        if(saveImage == null){
            try {
                ImageIO.write(image, format, f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            try {
                ImageIO.write(saveImage, format, f);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
 
    public void snapshot() {
        try {
            Robot robot = new Robot();
            Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
            image = robot.createScreenCapture(new Rectangle(0, 0, d.width,
                    d.height));
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
    
    public class ImgPanel extends Panel{
		private static final long serialVersionUID = -5278986419750012910L;
		Image img;
    	public ImgPanel(Image img){
    		this.img = img;
    	}
    	
    	@Override
    	public void paint(Graphics g) {
    		super.paint(g);
    		g.drawImage(img, 20,20,40,40, this);//其中第二到第五个参数分别为x,y,width,height
    	}
    }
}