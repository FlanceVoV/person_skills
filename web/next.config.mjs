/** @type {import('next').NextConfig} */
const nextConfig = {
  reactStrictMode: true,
  /** 静态导出到 `out/`，`pack-build.bat` 将拷入 `build/dist`；与 FastAPI 同端口，无需独立 Node 服务 */
  output: "export",
  images: { unoptimized: true },
};

export default nextConfig;
