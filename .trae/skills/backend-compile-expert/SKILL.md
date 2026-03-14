---
name: "backend-compile-expert"
description: "解决Spring Boot Maven后端编译问题，尤其是Lombok注解处理器不工作的错误。Invoke when user asks to compile backend or encounters Lombok-related compilation errors."
---

# Backend Compile Expert

本技能专门用于解决 Spring Boot Maven 后端项目的编译问题，特别是 Lombok 注解处理器不工作导致的编译错误。

## 常见问题症状

编译时出现以下错误：
- `找不到符号: 方法 setXxx()` 
- `找不到符号: 方法 getXxx()`
- Lombok 的 `@Data`、`@Getter`、`@Setter` 注解不生效

## 诊断步骤

### 1. 检查 pom.xml 配置

检查 Lombok 依赖和 maven-compiler-plugin 配置：

```xml
<properties>
    <java.version>11</java.version>
    <lombok.version>1.18.24</lombok.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>${lombok.version}</version>
        <scope>provided</scope>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>3.8.1</version>
            <configuration>
                <source>${java.version}</source>
                <target>${java.version}</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>${lombok.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

### 2. 检查 Lombok 版本兼容性

- Lombok 1.18.24 是一个稳定版本
- 确保 maven-compiler-plugin 版本不是 3.11.0（已知与 Lombok 有兼容性问题）
- 推荐使用 3.8.1 或 3.10.1

### 3. 清理并重新编译

```bash
mvn clean compile
```

### 4. 检查 IDE 缓存

- 如果使用 IDEA，尝试 File -> Invalidate Caches
- 删除 target 目录后重新编译

## 快速修复命令

```bash
cd backend
mvn clean compile -U
```

如果仍然失败，检查是否需要：
1. 更新 Lombok 版本
2. 调整 maven-compiler-plugin 版本
3. 检查是否有其他注解处理器冲突
