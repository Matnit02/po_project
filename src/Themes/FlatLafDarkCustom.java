package Themes;

import com.formdev.flatlaf.FlatDarkLaf;

public class FlatLafDarkCustom
	extends FlatDarkLaf
{
	public static final String NAME = "FlatLafDarkCustom";

	public static boolean setup() {
		return setup( new FlatLafDarkCustom() );
	}

	public static void installLafInfo() {
		installLafInfo( NAME, FlatLafDarkCustom.class );
	}

	@Override
	public String getName() {
		return NAME;
	}
}
