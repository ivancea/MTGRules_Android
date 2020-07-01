package xyz.ivancea.mtgrules.model;

import android.net.Uri;

import java.net.URI;
import java.nio.charset.Charset;
import java.time.LocalDate;

import lombok.Data;

@Data
public class RulesSource {
	private final URI uri;
	private final LocalDate date;
	private final Charset encoding;
}
