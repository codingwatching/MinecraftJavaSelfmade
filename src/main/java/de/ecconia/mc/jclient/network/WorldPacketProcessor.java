package de.ecconia.mc.jclient.network;

import de.ecconia.mc.jclient.PrimitiveDataDude;
import de.ecconia.mc.jclient.gui.chunkmap.ChunkMap;
import de.ecconia.mc.jclient.gui.monitor.L;
import de.ecconia.mc.jclient.network.packeting.GenericPacket;
import de.ecconia.mc.jclient.network.packeting.PacketReader;
import de.ecconia.mc.jclient.network.packeting.PacketThread;
import old.reading.helper.Provider;

public class WorldPacketProcessor extends PacketThread
{
	private final ChunkMap cMap;
	
	public WorldPacketProcessor(PrimitiveDataDude dataDude)
	{
		super("WorldPacketThread", dataDude);
		
		cMap = new ChunkMap();
		L.addCustomPanel("Chunks", cMap);
	}
	
	@Override
	protected void process(GenericPacket packet)
	{
		Provider p = packet.getProvider();
		int id = packet.getId();
		PacketReader reader = new PacketReader(p.readBytes(p.remainingBytes()));
		
		if(id == 0x22)
		{
			logPacket("Chunk Data");
			
			logData("Loaded chunk:");
			int x = reader.readInt();
			int y = reader.readInt();
			logData("> X: " + x + " Y: " + y);
			boolean wholeChunk = reader.readBoolean();
			logData("> Whole chunk: " + wholeChunk);
			logData("> Subchunk map: " + asBin(reader.readCInt(), 16));
			
			cMap.load(x, y);
			//Irrelevant for now:
			//int chunkDataSize = reader.readCInt();
			//logData("Chunk data size: " + chunkDataSize);
			
		}
		else if(id == 0x0B)
		{
			logPacket("Block changed");
		}
		else if(id == 0x0F)
		{
			logPacket("Block change multi");
		}
		else if(id == 0x1F)
		{
			logPacket("Unload chunk");
			
			logData("Unloaded chunk:");
			logData("> X: " + reader.readInt() + " Y: " + reader.readInt());
		}
	}
	
	private String asBin(int i, int length)
	{
		String bin = Integer.toBinaryString(i);
		
		while(bin.length() < length)
		{
			bin = '0' + bin;
		}
		
		return bin;
	}
	
	private void logPacket(String name)
	{
		L.writeLineOnChannel("World: Packets", name);
	}
	
	private void logData(String message)
	{
		L.writeLineOnChannel("Content", message);
	}
}
