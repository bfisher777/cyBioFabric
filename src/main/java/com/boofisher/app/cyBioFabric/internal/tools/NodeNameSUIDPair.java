package com.boofisher.app.cyBioFabric.internal.tools;

/**
 * Class represents a node name node model suid pair
 * @author Ben
 *
 */
public class NodeNameSUIDPair implements Comparable<NodeNameSUIDPair>{

	private Long suid;
	private String name;
	
	public NodeNameSUIDPair(Long suid, String name){
		this.suid = suid;
		this.name = name;
	}	
	
	public String getName(){ return name; }
	public Long getSUID(){ return suid; }
	
	@Override
	public boolean equals(Object otherName){
		if(otherName instanceof NodeNameSUIDPair){
			return this.name.equals(((NodeNameSUIDPair) otherName).getName());
		}else{
			return false;
		}
	}
	
	@Override
	public int hashCode(){
		return this.name.hashCode();
	}

	@Override
	public int compareTo(NodeNameSUIDPair o) {
		// TODO Auto-generated method stub
		return this.getName().compareTo(o.getName());
	}
}
