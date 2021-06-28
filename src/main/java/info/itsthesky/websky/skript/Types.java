package info.itsthesky.websky.skript;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import info.itsthesky.websky.tools.WebServer;
import info.itsthesky.websky.tools.WebServerBuilder;
import org.jetbrains.annotations.NotNull;

public class Types {

	static  {
		Classes.registerClass(new ClassInfo<>(WebServer.class, "webserver")
				.user("webservers?")
				.name("Webserver")
				.description("Represent a web server, opened or not, started or not, with port anf index file.")
				.since("0.1")
				.parser(new Parser<WebServer>() {
					@Override
					public WebServer parse(final @NotNull String s, final ParseContext context) {
						return null;
					}

					@Override
					public @NotNull String toString(@NotNull WebServer server, int flags) {
						return server.getName();
					}

					@Override
					public @NotNull String toVariableNameString(@NotNull WebServer server) {
						return server.getName();
					}

					@Override
					public @NotNull String getVariableNamePattern() {
						return "[a-z ]+";
					}
				}));
		Classes.registerClass(new ClassInfo<>(WebServerBuilder.class, "webserverbuilder")
				.user("webserverbuilders?")
				.name("Webserver Builder")
				.description("Represent a web server builder, which is here to handle the web server creation.")
				.since("0.1")
				.parser(new Parser<WebServerBuilder>() {
					@Override
					public WebServerBuilder parse(final @NotNull String s, final ParseContext context) {
						return null;
					}

					@Override
					public @NotNull String toString(@NotNull WebServerBuilder builder, int flags) {
						return builder.getName();
					}

					@Override
					public @NotNull String toVariableNameString(@NotNull WebServerBuilder builder) {
						return builder.getName();
					}

					@Override
					public @NotNull String getVariableNamePattern() {
						return "[a-z ]+";
					}
				}));
	}

}