package se.lexicon.g58todoapp.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Setter
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString
@Entity
@Table(name = "todos")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private Boolean completed = false;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(insertable = false)
    private LocalDateTime updatedAt;

    private LocalDateTime dueDate;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Person assignedTo;

    @ToString.Exclude
    @OneToMany(mappedBy = "todo" , cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true )
    private Set<Attachment> attachments = new HashSet<>();

    public Todo(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public Todo(String title, String description, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }

    public Todo(String title, String description, Boolean completed, LocalDateTime dueDate) {
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.dueDate = dueDate;
    }

    public Todo(String title, String description, LocalDateTime dueDate, Person assignedTo) {
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.assignedTo = assignedTo;
    }

    // helper methods for managing attachments
    public void addAttachment(Attachment attachment) {
        if (attachments == null) {
            attachments = new HashSet<>();
        }
        attachments.add(attachment);
        attachment.setTodo(this); // sync back-reference
    }

    public void removeAttachment(Attachment attachment) {
        attachments.remove(attachment);
        attachment.setTodo(null); // disconnect both ways
    }

    public boolean isOverdue() {
        return dueDate.isBefore(LocalDateTime.now());
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Todo todo = (Todo) o;
        return getId() != null && Objects.equals(getId(), todo.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
