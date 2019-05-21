package game2048;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.text.html.HTMLDocument.Iterator;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.ListIterator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;


public class game2048 extends JFrame {

	/**
	 * contentPane是整个JPanel
	 * label用于显示当前信息
	 * labeldown显示输赢
	 * grids是游戏区域所有小方格组成的JPanel
	 * grid是一个ArrayList，一个元素表示一个2048游戏中的小方格
	 * isfilled用于判断16个小格子是否被填充
	 * lose表示当前游戏是否输了
	 * gridint是每个界面中小格子对应的整数
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	public JLabel label = new JLabel("score: " + "0",JLabel.CENTER);
	public JLabel labeldown = new JLabel("be happy!",JLabel.CENTER);
	public JPanel grids = new JPanel();
	public ArrayList<JLabel> grid = new ArrayList<JLabel>();
	
	public boolean []isfilled = new boolean[16];
	public boolean lose = false;
	public int []gridint = new int[16];
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					game2048 frame = new game2048();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public game2048() {
		
		/*--------界面--------*/
		super("2048有你好玩的");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 320, 380);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		contentPane.add(label, BorderLayout.NORTH);
		contentPane.add(labeldown,BorderLayout.SOUTH);
		
		grids.setBounds(100, 100, 320, 320);
		contentPane.add(grids, BorderLayout.CENTER);
		grids.setLayout(new GridLayout(4, 4, 0, 0));
		
		for(int i = 0; i < 16; ++i) {
			grid.add(new JLabel("",JLabel.CENTER));
			grid.get(i).setSize(80,80);
			grid.get(i).setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
			grid.get(i).setOpaque(true);
			grid.get(i).setFont(new Font("粗体", Font.PLAIN, 15));
			grids.add(grid.get(i));
			isfilled[i] = false;
		}
		
		/*--------随机设置最初的2所在的位置并画界面--------*/
		int []startinit = ThreadLocalRandom.current().ints(0,16).distinct().limit(1).toArray();
		gridint[startinit[0]] = 2;
		isfilled[startinit[0]] = true;
		producegrid();
		drawgrid();
		lose = false;
		
		this.addKeyListener(new KeyListener(){
			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				int code = e.getKeyCode();
				//left
				if(code == KeyEvent.VK_LEFT) {
					moveleft();
				}
				//up
				else if(code == KeyEvent.VK_UP) {
					moveup();
				}
				//right
				else if(code == KeyEvent.VK_RIGHT) {
					moveright();
				}
				//down
				else if(code == KeyEvent.VK_DOWN) {
					movedown();
				}
				if(lose) {
					labeldown.setText("game over");
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
			}
		});
	}

	/*------------画界面-------------*/
	public void drawgrid() {
		for(int i = 0; i < 16; ++i) {
			if(isfilled[i]) {
				grid.get(i).setText("" + gridint[i]);
				// 设置颜色
				switch(gridint[i]) {
					case 2:
						grid.get(i).setBackground(new Color(255, 250, 240));
						break;
					case 4:
						grid.get(i).setBackground(new Color(255, 239, 213));
						break;
					case 8:
						grid.get(i).setBackground(new Color(244, 164, 96));
						break;
					case 16:
						grid.get(i).setBackground(new Color(255, 99, 71));
						break;
					case 32:
						grid.get(i).setBackground(new Color(255, 69, 0));
						break;
					case 64:
						grid.get(i).setBackground(new Color(255, 0, 0));
						break;
					case 128:
						grid.get(i).setBackground(new Color(255, 236, 139));
						break;
					case 256:
						grid.get(i).setBackground(new Color(255, 255, 0));
						break;
					case 512:
						grid.get(i).setBackground(new Color(190, 237, 199));
						break;
					case 1024:
						grid.get(i).setBackground(new Color(160, 238, 225));
						break;
					case 2048:
						grid.get(i).setBackground(new Color(25, 202, 173));
						break;
					case 4096:
						grid.get(i).setBackground(new Color(236, 173, 158));
						break;
					case 8192:
						grid.get(i).setBackground(new Color(244, 96, 108));
						break;
					default:
						grid.get(i).setBackground(new Color(255, 105, 180));
						break;
				}
			}
			else {
				grid.get(i).setText("");
				grid.get(i).setBackground(new Color(220,220,220));
			}
		}
		label.setText("score: " + getscore());
	}
	
	/*--------判断还能否移动---------*/
	public boolean canmove() {
		lose = !(canmoveright()||canmoveleft()||canmoveup()||canmovedown());
		return !lose;
	}
	/*--------判断还能否right移---------*/
	public boolean canmoveright() {
		for(int i = 3; i >= 0; --i) {
			for(int j = 2; j >= 0; --j) {
				if(isfilled[4*i+j]) {
					if((!isfilled[4*i+j+1])||(gridint[4*i+j+1] == gridint[4*i+j])) {
						return true;
					}
				}
			}
		}
		return false;
	}
	/*--------判断还能否left移---------*/
	public boolean canmoveleft() {
		for(int i = 3; i >= 0; --i) {
			for(int j = 1; j < 4; ++j) {
				if(isfilled[4*i+j]) {
					if((!isfilled[4*i+j-1])||(gridint[4*i+j-1] == gridint[4*i+j])) {
						return true;
					}
				}
			}
		}
		return false;
	}
	/*--------判断还能否up移---------*/
	public boolean canmoveup() {
		for(int j = 3; j >= 0; --j) {
			for(int i = 1; i < 4; ++i) {
				if(isfilled[4*i+j]) {
					if((!isfilled[4*i+j-4])||(gridint[4*i+j-4] == gridint[4*i+j])) {
						return true;
					}
				}
			}
		}
		return false;
	}
	/*--------判断还能否down移---------*/
	public boolean canmovedown() {
		for(int j = 3; j >= 0; --j) {
			for(int i = 2; i >= 0; --i) {
				if(isfilled[4*i+j]) {
					if((!isfilled[4*i+j+4])||(gridint[4*i+j+4] == gridint[4*i+j])) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/*--------进行right移--------*/
	public void moveright() {
		if(canmoveright()) {
			removerightblank();
			for(int i = 3; i >= 0; --i) {
				for(int j = 2; j >= 0; --j) {
					if(isfilled[4*i+j]) {
						if(gridint[4*i+j+1] == gridint[4*i+j]) {
							gridint[4*i+j+1] += gridint[4*i+j];
							gridint[4*i+j] = 0;
							isfilled[4*i+j] = false;
							removerightblank();
						}
					}
				}
			}
			producegrid();
			canmove();
			drawgrid();
		}
	}
	/*--------向right清除空格--------*/
	public void removerightblank() {
		int k;
		for(int i = 3; i >= 0; --i) {
			for(int j = 2; j >= 0; --j) {
				k = j;
				if(isfilled[4*i+j]) {
					while(k<3&&(!isfilled[4*i+k+1])) {
						boolean temp = isfilled[4*i+k];
						isfilled[4*i+k] = isfilled[4*i+k+1];
						isfilled[4*i+k+1] = temp;
						
						int tem = gridint[4*i+k];
						gridint[4*i+k] = gridint[4*i+k+1];
						gridint[4*i+k+1] = tem;

						++k;
					}
				}
			}
		}
	}
	/*--------进行left移--------*/
	public void moveleft() {
		if(canmoveleft()) {
			removeleftblank();
			for(int i = 3; i >= 0; --i) {
				for(int j = 1; j < 4; ++j) {
					if(isfilled[4*i+j]) {
						if(gridint[4*i+j-1] == gridint[4*i+j]) {
							gridint[4*i+j-1] += gridint[4*i+j];
							gridint[4*i+j] = 0;
							isfilled[4*i+j] = false;
							removeleftblank();
						}
					}
				}
			}
			producegrid();
			canmove();
			drawgrid();
		}
	}
	/*--------向left清除空格--------*/
	public void removeleftblank() {
		int k;
		for(int i = 3; i >= 0; --i) {
			for(int j = 1; j < 4; ++j) {
				k = j;
				if(isfilled[4*i+j]) {
					while(k>0&&(!isfilled[4*i+k-1])) {
						boolean temp = isfilled[4*i+k];
						isfilled[4*i+k] = isfilled[4*i+k-1];
						isfilled[4*i+k-1] = temp;
						
						int tem = gridint[4*i+k];
						gridint[4*i+k] = gridint[4*i+k-1];
						gridint[4*i+k-1] = tem;

						--k;
					}
				}
			}
		}
	}
	/*--------进行up移--------*/
	public void moveup() {
		if(canmoveup()) {
			removeupblank();
			for(int j = 3; j >= 0; --j) {
				for(int i = 1; i < 4; ++i) {
					if(isfilled[4*i+j]) {
						if(gridint[4*i+j-4] == gridint[4*i+j]) {
							gridint[4*i+j-4] += gridint[4*i+j];
							gridint[4*i+j] = 0;
							isfilled[4*i+j] = false;
							removeupblank();
						}
					}
				}
			}
			producegrid();
			canmove();
			drawgrid();
		}
	}
	/*--------向up清除空格--------*/
	public void removeupblank() {
		int k;
		for(int j = 3; j >= 0; --j) {
			for(int i = 1; i < 4; ++i) {
				k = i;
				if(isfilled[4*i+j]) {
					while(k>0&&(!isfilled[4*k+j-4])) {
						boolean temp = isfilled[4*k+j];
						isfilled[4*k+j] = isfilled[4*k+j-4];
						isfilled[4*k+j-4] = temp;
						
						int tem = gridint[4*k+j];
						gridint[4*k+j] = gridint[4*k+j-4];
						gridint[4*k+j-4] = tem;

						--k;
					}
				}
			}
		}
	}
	/*--------进行down移--------*/
	public void movedown() {
		if(canmovedown()) {
			removedownblank();
			for(int j = 3; j >= 0; --j) {
				for(int i = 2; i >= 0; --i) {
					if(isfilled[4*i+j]) {
						if(gridint[4*i+j+4] == gridint[4*i+j]) {
							gridint[4*i+j+4] += gridint[4*i+j];
							gridint[4*i+j] = 0;
							isfilled[4*i+j] = false;
							removedownblank();
						}
					}	
				}
			}
			producegrid();
			canmove();
			drawgrid();
		}
	}
	/*--------向down清除空格--------*/
	public void removedownblank() {
		int k;
		for(int j = 3; j >= 0; --j) {
			for(int i = 2; i >= 0; --i) {
				k = i;
				if(isfilled[4*i+j]) {
					while(k<3&&(!isfilled[4*k+j+4])) {
						boolean temp = isfilled[4*k+j];
						isfilled[4*k+j] = isfilled[4*k+j+4];
						isfilled[4*k+j+4] = temp;
						
						int tem = gridint[4*k+j];
						gridint[4*k+j] = gridint[4*k+j+4];
						gridint[4*k+j+4] = tem;

						++k;
					}
				}
			}
		}
	}

	/*------------生成随机新块(2或4)--------------*/
	public void producegrid() {
		int []pos = ThreadLocalRandom.current().ints(0,numofblank()).limit(1).toArray();
		int []val = ThreadLocalRandom.current().ints(0,2).limit(1).toArray();
		int cnt = 0;
		for(int i = 0; i < 16; ++i) {
			if(!isfilled[i]) {
				++cnt;
				if(cnt == pos[0] + 1) {
					isfilled[i] = true;
					gridint[i] = (val[0]+1)*2;
				}
			}
		}
	}
	/*--------统计空块总数----------*/
	public int numofblank() {
		int sum = 0;
		for(int i = 0; i < 16; ++i) {
			if(!isfilled[i])
				++sum;
		}
		return sum;
	}
	
	public int getscore() {
		int sum = 0;
		for(int i = 0; i < 16; ++i) {
			sum += gridint[i];
		}
		return sum;
	}
}
