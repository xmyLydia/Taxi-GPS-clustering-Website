package com.cn.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fragment {
 int id ;
 List<Taxi> fragment;
 List<MopsiEntity> fragmentMopsi;
 double[][] similarity;//�켣֮�����ƶ�
 ArrayList<Integer> indexEPS ;//�������ڵĹ켣ID
 int Num_eps;//�������ж������켣
 public void setID(int id){
	this.id = id;
 }
 public void setfragment(List<Taxi> fragment){
	 this.fragment = fragment;
 }
 public void setfragmentMopsi(List<MopsiEntity> fragment){
	 this.fragmentMopsi = fragment;
 }
 public int getID(){
	 return id;
 }
 public List<Taxi> getFragment(){
	 return fragment;
 }
 public void setSimilarity(double[][] similarity){
	 this.similarity= similarity;
 }
 public double[][] getSimilarity(){
	 return similarity;
 }
 public ArrayList<Integer> GetindexEPS(){
	 return indexEPS;
 }
 //�������켣��Ӧ������켣��id����ɵ�list
 public void setIndexEps(ArrayList<Integer> list){
	 indexEPS= list;
 }
  
  
}
