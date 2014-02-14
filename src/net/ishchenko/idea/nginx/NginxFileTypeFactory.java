package net.ishchenko.idea.nginx;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import net.ishchenko.idea.nginx.configurator.NginxServersConfiguration;
import org.jetbrains.annotations.NotNull;

public class NginxFileTypeFactory extends FileTypeFactory {

	@Override
	public void createFileTypes(@NotNull FileTypeConsumer fileTypeConsumer)
	{
		NginxFileType fileType = NginxFileType.INSTANCE;
		fileTypeConsumer.consume(fileType, fileType.getDefaultExtension());
	}
}
