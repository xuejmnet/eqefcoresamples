// See https://aka.ms/new-console-template for more information

using System.Diagnostics;
using ConsoleApp4;
using efcoreeq.Domain;
using Microsoft.EntityFrameworkCore;
using Microsoft.Extensions.Logging;

Console.WriteLine("Hello, World!");



// 配置DbContext选项
var optionsBuilder = new DbContextOptionsBuilder<MyDbContext>();
optionsBuilder.UseMySql("server=localhost;port=3316;database=eq_ef_db;user=root;password=root;",new MySqlServerVersion(new Version()));
optionsBuilder.LogTo(Console.WriteLine,LogLevel.Information) // 输出到控制台
    .EnableSensitiveDataLogging() // 显示参数值（可选）
    .EnableDetailedErrors();
// 直接创建DbContext实例
using (var context = new MyDbContext(optionsBuilder.Options))
{
    try
    {
        
        for (int i = 0; i < 10; i++)
        {
            var dateTime = DateTime.Now.AddDays(-7);
            var stopwatch = new Stopwatch();
            stopwatch.Start();
            var users = context.Set<User>()
                .AsNoTracking()
                .Where(u => u.Comments.Any(c =>
                    c.CreatedAt >= dateTime &&
                    c.Post.Category.Name ==".NET"))
                .Select(u =>new
                {
                    u.Id,
                    u.Username,
                    CommentsCount = u.Comments.Count(c =>
                        c.CreatedAt >= dateTime &&
                        c.Post.Category.Name ==".NET")
                })
                .OrderByDescending(u => u.CommentsCount)
                .Take(5)
                .ToList();
            stopwatch.Stop();
            Console.WriteLine("查询耗时:"+stopwatch.Elapsed+"(ms)");
        }
        for (int i = 0; i < 10; i++)
        {
            var dateTime = DateTime.Now.AddDays(-7);
            var stopwatch = new Stopwatch();
            stopwatch.Start();
            var users = context.Set<User>()
                .AsNoTracking()
                .Where(u =>u.Comments.Any())
                .OrderByDescending(u => u.Comments
                    .Count(c =>
                        c.CreatedAt >= dateTime &&
                        c.Post.Category.Name ==".NET")
                )
                .Take(5)
                .ToList();
            stopwatch.Stop();
            Console.WriteLine("查询耗时:"+stopwatch.Elapsed+"(ms)");
        }
    }
    catch (Exception ex)
    {
        Console.WriteLine($"错误: {ex.Message}");
    }
}