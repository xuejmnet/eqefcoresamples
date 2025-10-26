namespace efcoreeq.Domain;

public class User
{
    public string Id { get; set; }
    public string Username { get; set; } = null!;
    public ICollection<Post> Posts { get; set; } = new List<Post>();
    public ICollection<Comment> Comments { get; set; } = new List<Comment>();
}

public class Post
{
    public string Id { get; set; }
    public string Content { get; set; } = null!;
    public string UserId { get; set; }
    public User User { get; set; } = null!;
    public string CategoryId { get; set; }
    public Category Category { get; set; } = null!;
    public ICollection<Comment> Comments { get; set; } = new List<Comment>();
}

public class Category
{
    public string Id { get; set; }
    public string Name { get; set; } = null!;
    public ICollection<Post> Posts { get; set; } = new List<Post>();
}

public class Comment
{
    public string Id { get; set; }
    public string UserId { get; set; }
    public User User { get; set; } = null!;
    public string PostId { get; set; }
    public Post Post { get; set; } = null!;
    public string Text { get; set; } = null!;
    public DateTime CreatedAt { get; set; }
}