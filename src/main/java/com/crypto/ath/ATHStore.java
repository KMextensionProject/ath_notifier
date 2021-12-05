package com.crypto.ath;

public interface ATHStore {

	double getCurrentATH();

	void updateATH(double price) throws Exception;

	boolean isAboveATH(double price);

}
