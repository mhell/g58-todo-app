package se.lexicon.g58todoapp.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attachments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class Attachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;
    private String fileType;

    @Lob
    // 1KB = 1024 bytes
    // 1MB = 1024 x 1024 = 1048576 bytes
    //@Column( length = 10485760 )  // 10 MB
    private byte[] data;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "todo_id", nullable = false)
    private Todo todo;

    public Attachment(String fileName, String fileType, byte[] data) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.data = data;
    }

    public void setTodo(Todo todo) {
        this.todo = todo;
        if (todo != null)
            todo.getAttachments().add(this);
    }
}
