import { defineConfig } from "vite";
import { resolve } from "node:path";

export default defineConfig({

    base: "/",

    build: {

        outDir:
            "../src/main/resources/static",

        emptyOutDir: true,

        rollupOptions: {

            input:
                resolve(process.cwd(), "index.html")
        }
    }
});