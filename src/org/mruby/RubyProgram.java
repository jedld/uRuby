package org.mruby;

import java.util.ArrayList;

import org.mruby.parser.Block;

public class RubyProgram {
	ArrayList<Block> blocks = new ArrayList<Block>();

	public void addBlock(Block block) {
		blocks.add(block);
	}

	public String toString() {
		StringBuffer buffer = new StringBuffer();
		int totalBlocks = 0;
		for(Block block : blocks) { totalBlocks +=block.getStatements().size();};
		buffer.append(totalBlocks);
		for(Block block : blocks) {
			buffer.append(block.toString(false));
		}
		return buffer.toString();
	}
}
