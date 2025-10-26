using efcoreeq.Domain;
using Microsoft.EntityFrameworkCore;

namespace ConsoleApp4;

public class MyDbContext:DbContext
{
    public MyDbContext(DbContextOptions<MyDbContext> options) : base(options)
    {
        
    }

   protected override void OnModelCreating(ModelBuilder modelBuilder)
{
    base.OnModelCreating(modelBuilder);

    // ========== User ==========
    modelBuilder.Entity<User>(entity =>
    {
        entity.ToTable("t_user");
        entity.HasKey(u => u.Id);

        entity.Property(u => u.Id)
              .HasColumnName("id")
              .HasMaxLength(36)
              .IsRequired();

        entity.Property(u => u.Username)
              .HasColumnName("username")
              .HasMaxLength(100)
              .IsRequired();

        // User 1 - n Post
        entity.HasMany(u => u.Posts)
              .WithOne(p => p.User)
              .HasForeignKey(p => p.UserId)
              .OnDelete(DeleteBehavior.Cascade);

        // User 1 - n Comment
        entity.HasMany(u => u.Comments)
              .WithOne(c => c.User)
              .HasForeignKey(c => c.UserId)
              .OnDelete(DeleteBehavior.Cascade);
    });

    // ========== Category ==========
    modelBuilder.Entity<Category>(entity =>
    {
        entity.ToTable("t_category");
        entity.HasKey(c => c.Id);

        entity.Property(c => c.Id)
              .HasColumnName("id")
              .HasMaxLength(36)
              .IsRequired();

        entity.Property(c => c.Name)
              .HasColumnName("name")
              .HasMaxLength(100)
              .IsRequired();

        // Category 1 - n Post
        entity.HasMany(c => c.Posts)
              .WithOne(p => p.Category)
              .HasForeignKey(p => p.CategoryId)
              .OnDelete(DeleteBehavior.Restrict);
    });

    // ========== Post ==========
    modelBuilder.Entity<Post>(entity =>
    {
        entity.ToTable("t_post");
        entity.HasKey(p => p.Id);

        entity.Property(p => p.Id)
              .HasColumnName("id")
              .HasMaxLength(36)
              .IsRequired();

        entity.Property(p => p.Content)
              .HasColumnName("content")
              .HasMaxLength(2000)
              .IsRequired();

        entity.Property(p => p.UserId)
              .HasColumnName("user_id")
              .HasMaxLength(36)
              .IsRequired();

        entity.Property(p => p.CategoryId)
              .HasColumnName("category_id")
              .HasMaxLength(36)
              .IsRequired();

        // Post 1 - n Comment
        entity.HasMany(p => p.Comments)
              .WithOne(c => c.Post)
              .HasForeignKey(c => c.PostId)
              .OnDelete(DeleteBehavior.Cascade);
    });

    // ========== Comment ==========
    modelBuilder.Entity<Comment>(entity =>
    {
        entity.ToTable("t_comment");
        entity.HasKey(c => c.Id);

        entity.Property(c => c.Id)
              .HasColumnName("id")
              .HasMaxLength(36)
              .IsRequired();

        entity.Property(c => c.UserId)
              .HasColumnName("user_id")
              .HasMaxLength(36)
              .IsRequired();

        entity.Property(c => c.PostId)
              .HasColumnName("post_id")
              .HasMaxLength(36)
              .IsRequired();

        entity.Property(c => c.Text)
              .HasColumnName("text")
              .HasMaxLength(1000)
              .IsRequired();

        entity.Property(c => c.CreatedAt)
              .HasColumnName("create_at")
              .HasColumnType("datetime(3)")
              .IsRequired();
    });
}
}
