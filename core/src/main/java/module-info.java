module atnum.content.core {

//	requires static lombok;

	requires com.artifex.mupdf;
	requires com.fasterxml.jackson.annotation;
	requires com.fasterxml.jackson.core;
	requires com.fasterxml.jackson.databind;
	requires com.fasterxml.jackson.dataformat.yaml;
	requires com.fasterxml.jackson.datatype.jdk8;
	requires com.fasterxml.jackson.datatype.jsr310;
	requires com.google.common;
	requires com.google.guice;
	requires commons.math3;
	requires java.desktop;
	requires java.logging;
	requires javax.inject;
	requires jlatexmath;
	requires JTransforms;
	requires org.apache.logging.log4j;
	requires org.apache.logging.log4j.core;
	requires org.apache.pdfbox;
	requires org.apache.fontbox;
	requires org.bouncycastle.provider;
	requires org.bouncycastle.pkix;
	requires org.knowm.xchart;

	exports atnum.content.core;
	exports atnum.content.core.app;
	exports atnum.content.core.app.configuration;
	exports atnum.content.core.app.configuration.bind;
	exports atnum.content.core.app.dictionary;
	exports atnum.content.core.audio;
	exports atnum.content.core.audio.bus;
	exports atnum.content.core.audio.bus.event;
	exports atnum.content.core.audio.device;
	exports atnum.content.core.audio.filter;
	exports atnum.content.core.audio.sink;
	exports atnum.content.core.audio.source;
	exports atnum.content.core.beans;
	exports atnum.content.core.bus;
	exports atnum.content.core.bus.event;
	exports atnum.content.core.codec;
	exports atnum.content.core.controller;
	exports atnum.content.core.converter;
	exports atnum.content.core.geometry;
	exports atnum.content.core.graphics;
	exports atnum.content.core.inject;
	exports atnum.content.core.input;
	exports atnum.content.core.io;
	exports atnum.content.core.model;
	exports atnum.content.core.model.action;
	exports atnum.content.core.model.listener;
	exports atnum.content.core.model.shape;
	exports atnum.content.core.net;
	exports atnum.content.core.pdf;
	exports atnum.content.core.presenter;
	exports atnum.content.core.recording;
	exports atnum.content.core.recording.action;
	exports atnum.content.core.recording.edit;
	exports atnum.content.core.recording.file;
	exports atnum.content.core.render;
	exports atnum.content.core.service;
	exports atnum.content.core.text;
	exports atnum.content.core.tool;
	exports atnum.content.core.util;
	exports atnum.content.core.view;

}