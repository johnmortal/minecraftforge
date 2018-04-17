package org.dadsmods;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import java.util.TreeMap;
import java.util.TreeSet;

import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BigMaze {
// 5 x 5 x 4 (counting boundary
// put stepping blocks on lowest x if x + y + z == 0 mod 2
// and on highest x if x + y + z == 1 mod 2
	
	// this is an interior size, 
	// meaning it is a size without floor, ceiling, or walls that separate rooms
	
	String[][] roomLayout;
	
	static int interiorXSize;
	static int interiorYSize;
	static int interiorZSize;

	static IBlockState obsidian = Blocks.OBSIDIAN.getDefaultState();
	static IBlockState glow = Blocks.GLOWSTONE.getDefaultState();
	static IBlockState glass = Blocks.GLASS.getDefaultState();
	static IBlockState stone = Blocks.STONE.getDefaultState();
	static IBlockState red = Blocks.RED_GLAZED_TERRACOTTA.getDefaultState();
	static IBlockState green = Blocks.GREEN_GLAZED_TERRACOTTA.getDefaultState();
	static IBlockState yellow = Blocks.YELLOW_GLAZED_TERRACOTTA.getDefaultState();
	static IBlockState xWall = yellow;
	static IBlockState yWall = green;
	static IBlockState zWall = red;
	
	@SubscribeEvent
	public void maze(LivingJumpEvent event) {
		World world = event.getEntity().getEntityWorld();
		if (! (event.getEntity() instanceof EntityPlayer)) return;
		int x = (int) Math.floor(event.getEntity().posX);
		int y = (int) Math.floor(event.getEntity().posY);
		int z = (int) Math.floor(event.getEntity().posZ);		
		createMaze(world, x, y, z, 3, 3, 3);
	}
	
	
	
	static void createMaze(World world, int x, int y, int z, int xSize, int ySize, int zSize) {
		if (world.isRemote) return; // this needs to run on the server world

		Maze maze = MazeGenerator.GenerateKruskalMaze3D(xSize, ySize, zSize);
		
		interiorXSize = 2;
		interiorYSize = 2;
		interiorZSize = 2;
		
		
		createOuterWalls(world, x, y, z, maze);
		createInternalFrame(world, x, y, z, maze);
		createInnerWalls(world, x, y, z, maze);
		
		
		
		
	}
	
	static void createInternalFrame(World world, int x, int y, int z, Maze maze) {
		int xStep = interiorXSize + 1;
		int yStep = interiorYSize + 1;
		int zStep = interiorZSize + 1;
		int xLength = xStep * maze.xSize + 1;
		int yLength = yStep * maze.ySize + 1;
		int zLength = zStep * maze.zSize + 1;
		
		for ( int j = 1; j < maze.ySize; j++) {
			for ( int k = 1; k < maze.zSize + 1; k++ ) {
				for ( int u = x + 1; u < x + xLength - 1; u ++ ) { 
					int v = y + j * yStep;
					int w = z + k * zStep;
					BlockPos pos = new BlockPos(u,v,w);
					world.setBlockState(pos, glow);					
				}
			}
		}
		
		for ( int k = 1; k < maze.zSize; k ++ ) {
			for ( int i = 0; i < maze.xSize; i ++ ) {
				for ( int v = y + 1; v < y + yLength - 1; v ++ ) {
					int u = x + i * xStep;
					int w = z + k * zStep;
					BlockPos pos = new BlockPos(u,v,w);
					world.setBlockState(pos, glow);
				}
			}
		}
		
		for ( int i = 1; i < maze.xSize; i ++ ) {
			for ( int j = 1; j < maze.ySize; j ++ ) {
				for ( int w = z + 1; w < z + zLength - 1; w ++ ) {
					int u = x + i * xStep;
					int v = y + j * yStep;
					BlockPos pos = new BlockPos(u,v,w);
					world.setBlockState(pos, glow);
				}
			}
		}

	
	}
	
	static void createOuterWalls(World world, int x, int y, int z, Maze maze) {
		int xStep = interiorXSize + 1;
		int yStep = interiorYSize + 1;
		int zStep = interiorZSize + 1;
		int xLength = xStep * maze.xSize + 1;
		int yLength = yStep * maze.ySize + 1;
		int zLength = zStep * maze.zSize + 1;
		
		// x outer walls
		for ( int j = 0; j < yLength; j ++ ) {
			for ( int k = 0; k < zLength; k ++ ) {
				BlockPos pos = new BlockPos(x, y + j, z + k );
				world.setBlockState(pos, xWall);
				pos = new BlockPos(x + xLength - 1, y + j, z + k );
				world.setBlockState(pos, xWall);
			}
		}
		
		
		for ( int i = 0; i < xLength; i ++ ) {
			for ( int j = 0; j < yLength; j ++ ) {
				BlockPos pos = new BlockPos(x + i, y + j, z);
				world.setBlockState(pos, zWall);
				pos = new BlockPos(x + i, y + j, z + zLength - 1);
				world.setBlockState(pos, zWall);
			}
		}
		
		for ( int i = 0; i < xLength; i ++ ) {
			for ( int k = 0; k < zLength; ++ k ) {
				BlockPos pos = new BlockPos(x + i, y, z + k);
				world.setBlockState(pos, yWall);
				pos = new BlockPos(x + i, y + yLength - 1, z + k);
				world.setBlockState(pos, yWall);
			}
		}
		
		
		
		
	}
	
	
	static void createInnerWalls(World world, int x, int y, int z, Maze maze) {
		int xStep = interiorXSize + 1;
		int yStep = interiorYSize + 1;
		int zStep = interiorZSize + 1;
		for ( int i = 0; i < maze.xSize; i ++ ) {
			for ( int j = 0; j < maze.ySize; j ++ ) {
				for ( int k = 0; k < maze.zSize; k ++ ) {
					// create interior x walls
					int xCorner = x + i * xStep;
					int yCorner = y + j * yStep;
					int zCorner = z + k * zStep;
					if ( maze.canMoveXUp(i, j, k) && i < maze.xSize - 1 ) {
						for ( int v = 0; v < interiorYSize; v ++ ) {
							for ( int w = 0; w < interiorZSize; w ++ ) {
								BlockPos pos = new BlockPos(xCorner + xStep, yCorner + v + 1, zCorner + w + 1);
								world.setBlockState(pos, xWall);
							}
						}
					}
					if ( maze.canMoveYUp(i, j, k) && j < maze.ySize - 1 ) {
						for ( int u = 0; u < interiorXSize; u ++ ) {
							for ( int w = 0; w < interiorZSize; w ++ ) {
								BlockPos pos = new BlockPos(xCorner + u + 1, yCorner + yStep, zCorner + w + 1);
								world.setBlockState(pos, yWall);
							}
						}
					}
					if ( maze.canMoveZUp(i, j, k) && k < maze.zSize - 1 ) {
						for ( int u = 0; u < interiorXSize; u ++ ) {
							for ( int v = 0; v < interiorYSize; v ++ ) {
								BlockPos pos = new BlockPos(xCorner + u + 1, yCorner + v + 1, zCorner + zStep + 1);
								world.setBlockState(pos, zWall);
							}
						}
					}
					BlockPos pos = new BlockPos(x,y,z);
					world.setBlockState(pos,obsidian);
					
				}
			}
		}
		
		
		
	}
	
	
	
}




class Maze
{
    // there is no good reason to 
    // explicitly model the boundary
    // for the exit of the maze, even if we make the exit
    // in the boundary of the maze we have 
    // to query for whether we are in the adjacent 
    // square INSIDE the maze in order to
    // allow moving to a point outside the maze
    // as legitimate
    // thus we do not model the boundary of the maze
    // and we model the exit as a point in the maze


    public int xSize, ySize, zSize;
    private Boolean[][][] canMoveXDownLayout;
    private Boolean[][][] canMoveYDownLayout;
    private Boolean[][][] canMoveZDownLayout;

    public Boolean canMoveXDown(int i, int j, int k)
    {
        return (i > 0 ) && canMoveXDownLayout[i - 1][j][k];
    }

    public Boolean canMoveXUp(int i, int j, int k)
    {
        return (i < xSize - 1 ) && canMoveXDownLayout[i][j][k];
    }
    
    public Boolean canMoveYDown(int i, int j, int k)
    {
        return (j > 0 ) && canMoveYDownLayout[i][j - 1][k];
    }

    public Boolean canMoveYUp(int i, int j, int k)
    {
        return (j < ySize - 1 ) && canMoveYDownLayout[i][j][k];
    }

    public Boolean canMoveZDown(int i, int j, int k)
    {
        return (k > 0 ) && canMoveYDownLayout[i][j][k - 1];
    }

    public Boolean canMoveZUp(int i, int j, int k)
    {
        return (k < zSize - 1 ) && canMoveZDownLayout[i][j][k];
    }


    public void blockXDown(int i, int j, int k)
    {
        canMoveXDownLayout[i - 1][j][k] = false;
    }

    public void blockXUp(int i, int j, int k)
    {
        canMoveXDownLayout[i][j][k] = false;
    }

    public void blockYDown(int i, int j, int k)
    {
        canMoveYDownLayout[i][j - 1][k] = false;
    }

    public void blockYUp(int i, int j, int k)
    {
        canMoveYDownLayout[i][j][k] = false;
    }

    public void blockZDown(int i, int j, int k)
    {
        canMoveZDownLayout[i][j][k - 1] = false;
    }

    public void blockZUp(int i, int j, int k)
    {
        canMoveZDownLayout[i][j][k] = false;
    }

    
    public void unblockXDown(int i, int j, int k)
    {
        canMoveXDownLayout[i - 1][j][k] = true;
    }

    public void unblockXUp(int i, int j, int k)
    {
        canMoveXDownLayout[i][j][k] = true;
    }

    public void unblockYDown(int i, int j, int k)
    {
        canMoveYDownLayout[i][j - 1][k] = true;
    }

    public void unblockYUp(int i, int j, int k)
    {
        canMoveYDownLayout[i][j][k] = true;
    }

    public void unblockZDown(int i, int j, int k)
    {
        canMoveZDownLayout[i][j][k - 1] = true;
    }

    public void unblockZUp(int i, int j, int k)
    {
        canMoveZDownLayout[i][j][k] = true;
    }



    public Maze(int xSizeIn, int ySizeIn, int zSizeIn)
    {
        xSize = xSizeIn;
        ySize = ySizeIn;
        zSize = zSizeIn;
        // we cannot move off the maze
        canMoveXDownLayout = new Boolean[xSize - 1][][];
        for ( int i = 0; i < xSize-1; i ++ ) {
        	canMoveXDownLayout[i] = new Boolean[ySize][];
        	for ( int j = 0; j < ySize; j ++ ) {
        		canMoveXDownLayout[i][j] = new Boolean[zSize];
        	}
        }

        canMoveYDownLayout = new Boolean[xSize][][];
        for ( int i = 0; i < xSize; i ++ ) {
        	canMoveYDownLayout[i] = new Boolean[ySize - 1][];
        	for ( int j = 0; j < ySize - 1; j ++ ) {
        		canMoveYDownLayout[i][j] = new Boolean[zSize];
        	}
        }

        canMoveZDownLayout = new Boolean[xSize][][];
        for ( int i = 0; i < xSize; i ++ ) {
        	canMoveZDownLayout[i] = new Boolean[ySize][];
        	for ( int j = 0; j < ySize; j ++ ) {
        		canMoveZDownLayout[i][j] = new Boolean[zSize - 1];
        	}
        }

        
        // so we only need width-1 x height wall bools (i.e. we don't need one on the left edge)
//        canMoveRightLayout = new Boolean[width - 1, height];
        // we cannot move off the maze
        // so we only need width x (height - 1) wall bools (i.e. we can't move off the bottom edge)
//        canMoveDownLayout = new Boolean[width, height - 1];
    }

    // this is another common form for a graph which is, for mazes, far more conicse than an incidence matrix
/*    public int[][] generateGraphConnections()
    {
        int nodeCount = xSize * ySize * zSize;
        List<int>[] tmpEdgeList = new List<int>[nodeCount];
        for (int j = 0; j < height; ++j )
        {
            for (int i = 0; i < width; ++i)
            {
                int nodeIndex = j * width + i;
                tmpEdgeList[nodeIndex] = new List<int>();
            }
        }


        for (int j = 0; j < height; ++j)
        {
            for (int i = 0; i < width; ++i)
            {
                int nodeIndex = j * width + i;
                int nodeUp = nodeIndex - width;
                int nodeDown = nodeIndex + width;
                int nodeLeft = nodeIndex - 1;
                int nodeRight = nodeIndex + 1;
                if (canMoveDown(i, j))
                {
                    tmpEdgeList[nodeIndex].Add(nodeDown);
                    tmpEdgeList[nodeDown].Add(nodeIndex);
                }
                if (canMoveUp(i, j))
                {
                    tmpEdgeList[nodeIndex].Add(nodeUp);
                    tmpEdgeList[nodeUp].Add(nodeIndex);
                }
                if (canMoveLeft(i, j))
                {
                    tmpEdgeList[nodeIndex].Add(nodeLeft);
                    tmpEdgeList[nodeLeft].Add(nodeIndex);
                }
                if (canMoveRight(i, j))
                {
                    tmpEdgeList[nodeIndex].Add(nodeRight);
                    tmpEdgeList[nodeRight].Add(nodeIndex);
                }
            }
        }
        int[][] edgeList = new int[nodeCount][];
        for ( int i = 0; i < nodeCount; ++i )
        {
            tmpEdgeList[i].Sort();
            edgeList[i] = tmpEdgeList[i].ToArray();
        }
        return edgeList;

    }


    public Boolean[,] generateGraphIndicenceMatrix()
    {
        int nodeCount = width * height;
        Boolean[,] incidenceMatrix = new Boolean[nodeCount, nodeCount];
        for (int j = 0; j < height; ++j)
        {
            for (int i = 0; i < width; ++i)
            {
                int nodeIndex = j * width + i;
                int nodeUp = nodeIndex - width;
                int nodeDown = nodeIndex + width;
                int nodeLeft = nodeIndex - 1;
                int nodeRight = nodeIndex + 1;
                if (canMoveDown(i,j))
                {
                    incidenceMatrix[nodeIndex, nodeDown] = true;
                    incidenceMatrix[nodeDown, nodeIndex] = true;                      
                }
                if (canMoveUp(i, j))
                {
                    incidenceMatrix[nodeIndex, nodeUp] = true;
                    incidenceMatrix[nodeUp, nodeIndex] = true;
                }
                if (canMoveLeft(i, j))
                {
                    incidenceMatrix[nodeIndex, nodeLeft] = true;
                    incidenceMatrix[nodeLeft, nodeIndex] = true;
                }
                if (canMoveRight(i, j))
                {
                    incidenceMatrix[nodeIndex, nodeRight] = true;
                    incidenceMatrix[nodeRight, nodeIndex] = true;
                }
            }
        }
        return incidenceMatrix;
    }
*/
}

class Position
{
    public int x;
    public int y;
    public int z;
    public Position(int xIn, int yIn, int zIn)
    {
        x = xIn;
        y = yIn;
        z = zIn;
    }
}



class Wall {
    // a wall is a cell address and a direction
    // thus they are somewhat 
    
    public static final int YDOWN = 0;
    public static final int XUP = 1;
    public static final int YUP = 2;
    public static final int XDOWN = 3;
    public static final int ZDOWN = 4;
    public static final int ZUP = 5;
    
    public int x;
    public int y;
    public int z;
    public int cellFace;

    // the coordinates of the cell opposite the wall are generated
    public int otherX;
    public int otherY;
    public int otherZ;
    
    public Wall(int xIn, int yIn, int zIn, int cellFaceIn)
    {
        x = xIn;
        y = yIn;
        z = zIn;
        cellFace = cellFaceIn;
        switch (cellFace) {
        
        	case XDOWN:
        		otherX = x - 1;
        		otherY = y;
        		otherZ = z;
        		break;
        	case XUP:
        		otherX = x + 1;
        		otherY = y;
        		otherZ = z;
        		break;
            case YDOWN:
                otherX = x;
                otherY = y - 1;
                otherZ = z;
                break;
            case YUP:
                otherX = x;
                otherY = y + 1;
                otherZ = z;
                break;
            case ZDOWN:
            	otherX = x;
            	otherY = y;
            	otherZ = z - 1;
            case ZUP:
            	otherX = x;
            	otherY = y;
            	otherZ = z + 1;
            default:
                otherX = -1;
                otherY = -1;
                otherZ = -1;
                break;
        }
    }


}




class MazeGenerator
{
    // given screen borders it is probably safer not to have a border line missing as an exit.
    // thus we will not explicitly model our maze as even having borders. You simply can't move outside 
    // the maze
    // If it is an M x N maze what we want to be able to do is 
    // (1) save it in a form where it can be loaded in an app
    // (2) attempt to solve it
    // (3) alter it based on what we get from attempting to solve it


    public static Maze GenerateRecursiveBacktrackerMaze(int xSize, int ySize, int zSize, int startX, int startY, int startZ)
    {
        // we identify cell (x,y) of a maize 
        // with the integer y * width + x 
        // for the purpose of this
        Maze maze = new Maze(xSize, zSize, zSize);
        Random random = new Random();

        int lastDirection = Wall.XDOWN; // start with an initial "last direction"

        Position startPosition = new Position(startX, startY, startZ); // start position 
        int cellCount = xSize * ySize * zSize;
        Position current = startPosition; // current 

        Stack<Position> stack = new Stack<Position>();
        TreeSet<Integer> visited = new TreeSet<Integer>();
        visited.add(startPosition.z * ySize * xSize + startPosition.y * xSize + startPosition.x);

        while ( visited.size() < cellCount )
        {
            ArrayList<Integer> unvisitedNeighbors = new ArrayList<Integer>();
            // populate unvisted neigbors
            int currentInt = current.z * ySize * xSize + current.y * xSize + current.x;
            if ((current.x > 0) && ( ! visited.contains(currentInt - 1)))
                unvisitedNeighbors.add(Wall.XDOWN);
            if ((current.x < xSize - 1) && ( ! visited.contains(currentInt + 1)))
                unvisitedNeighbors.add(Wall.XUP);
            if ((current.y > 0) && ( ! visited.contains(currentInt - xSize)))
                unvisitedNeighbors.add(Wall.YDOWN);
            if ((current.y < ySize - 1) && ( ! visited.contains(currentInt + xSize)))
                unvisitedNeighbors.add(Wall.YUP);
            if ((current.z > 0) && ( ! visited.contains(currentInt - xSize * ySize)))
                unvisitedNeighbors.add(Wall.ZDOWN);
            if ((current.z < zSize - 1) && ( ! visited.contains(currentInt + xSize * ySize)))
                unvisitedNeighbors.add(Wall.ZUP);
            if ( unvisitedNeighbors.size() > 0 )
            {
                int nextDirection;
                nextDirection = unvisitedNeighbors.get(random.nextInt(unvisitedNeighbors.size()));
                stack.push(current);
                switch (nextDirection)
                {
                    case Wall.YDOWN:
                        maze.unblockYDown(current.x, current.y,current.z);
                        current.y = current.y - 1;
                        break;
                    case Wall.YUP:
                        maze.unblockYUp(current.x, current.y, current.z);
                        current.y = current.y + 1;
                        break;
                    case Wall.XDOWN:
                        maze.unblockXDown(current.x, current.y, current.z);
                        current.x = current.x - 1;
                        break;
                    case Wall.XUP:
                        maze.unblockXUp(current.x, current.y, current.z);
                        current.x = current.x + 1;
                        break;
                    case Wall.ZDOWN:
                        maze.unblockXDown(current.x, current.y, current.z);
                        current.z = current.z - 1;
                        break;
                    case Wall.ZUP:
                        maze.unblockXUp(current.x, current.y, current.z);
                        current.z = current.z + 1;
                        break;
                }
                visited.add(current.z * xSize * zSize + current.y * xSize + current.x);
            }
            else if ( stack.size() > 0 )
            {
                current = stack.pop();
            }
        }
        return maze;
    }



    public static Maze GenerateKruskalMaze3D(int xSize, int ySize, int zSize)
    {
        Maze maze = new Maze(xSize, ySize, zSize);



        Random random = new Random();
        // start with a filled maze, so path generation is very rapid
        

        // choose two intermediate points the solution must hit , possibly make them symmetric about the center point

        // remove edges one at a time, keeping a pool of the remaining possibilities
        // if removing an edge ever creates a loop, put it back in the maze but remove it from the pool of possibilities
        // when the pool of possibilities is empty, consider that the completed maze
        // of necessity it has a single connected component (else removing a wall possible), and each point has a unique path to each other point
        TreeMap<Long, Wall> poolOfWallsRandomizer = new TreeMap<Long, Wall>();
        long value;
        for ( int i = 0; i < xSize; ++ i )
        {
            for ( int j = 0; j < ySize; ++ j )
            {
            	for ( int k = 0; k < zSize; ++ k) {
                    if (i < xSize - 1)
                    {
                        Wall wall = new Wall(i, j, k, Wall.XUP);
                        do
                        {
                            value = random.nextLong();
                        } while (poolOfWallsRandomizer.containsKey(value));
                        poolOfWallsRandomizer.put(value, wall);
                    }
                    if ( j < ySize - 1 )
                    {
                        Wall wall = new Wall(i, j, k, Wall.YUP);
                        do
                        {
                            value = random.nextLong();
                        } while (poolOfWallsRandomizer.containsKey(value));
                        poolOfWallsRandomizer.put(value, wall);
                    }
                    if ( k < zSize - 1 )
                    {
                        Wall wall = new Wall(i, j, k, Wall.ZUP);
                        do
                        {
                            value = random.nextLong();
                        } while (poolOfWallsRandomizer.containsKey(value));
                        poolOfWallsRandomizer.put(value, wall);
                    }
            		
            	}
            }
        }

        Stack<Wall> poolOfWalls = new Stack<Wall>();
        for (Wall wall : poolOfWallsRandomizer.values()) poolOfWalls.push(wall);


        // this will keep track of which connected component each cell belongs to
        // simply assign an integer to each cell
        // you can only remove a wall between two cells with different integers
        // when you remove a wall between cells with integers A and B then
        // change every cell with a B to an A 
        int[][][] component = new int[xSize][][];
        for (int i = 0; i < xSize; i++ ) {
        	component[i] = new int[ySize][];
        	for ( int j = 0; j < ySize; j ++ ) {
        		component[i][j] = new int[zSize];
        	}
        }
        int count = 0;
        for ( int i = 0; i < xSize; i++ )
        {
            for ( int j = 0; j < ySize; j ++ )
            {
            	for ( int k = 0; k < zSize; k ++ ) {
                    component[i][j][k] = count++;
            	}
            }
        }

        while ( poolOfWalls.size() > 0 )
        {
            Wall wall = poolOfWalls.pop();
            int cellComponent = component[wall.x][wall.y][wall.z];
            int otherComponent = component[wall.otherX][wall.otherY][wall.otherZ]; // other side of the wall 
            if ( cellComponent == otherComponent ) continue; // this would create a loop, so discard it
            switch (wall.cellFace) // if not part of the same component remove the wall
            {
                case Wall.XDOWN:
                    maze.unblockXDown(wall.x, wall.y, wall.z);
                    break;
                case Wall.XUP:
                    maze.unblockXUp(wall.x, wall.y, wall.z);
                    break;
                case Wall.YDOWN:
                    maze.unblockYDown(wall.x, wall.y, wall.z);
                    break;
                case Wall.YUP:
                    maze.unblockYUp(wall.x, wall.y, wall.z);
                    break;
                case Wall.ZDOWN:
                    maze.unblockZDown(wall.x, wall.y, wall.z);
                    break;
                case Wall.ZUP:
                    maze.unblockZUp(wall.x, wall.y, wall.z);
                    break;
            }
            int target = Math.max(cellComponent, otherComponent); 
            int destination = Math.min(cellComponent, otherComponent);
            for ( int i = 0; i < xSize; i ++ )
            {
                for ( int j = 0; j < ySize; j ++ )
                {
                	for ( int k = 0; k < zSize; k ++ ) 
                	{
                        if (component[i][j][k] == target) component[i][j][k] = destination;                		
                	}
                }
            }
        }
        return maze;
    } 
    



}
