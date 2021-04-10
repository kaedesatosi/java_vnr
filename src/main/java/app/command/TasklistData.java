package app.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TasklistData {

	private String imageName;
	private int pid;

	public String toString() {
		return pid + "--" + imageName;
	}
}
