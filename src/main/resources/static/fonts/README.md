# Fonts Directory

Place your TTF font files in this directory.

## How to use:

1. Copy your `.ttf` font file to this directory
2. Update the `@font-face` rule in `index.html`:
   - Change `'CustomFont'` to your desired font name
   - Change `'/fonts/your-font-name.ttf'` to match your actual font filename

## Example:

If you have a font file named `MyCustomFont.ttf`:
- Place it in this directory: `src/main/resources/static/fonts/MyCustomFont.ttf`
- Update the CSS in `index.html`:
  ```css
  @font-face {
      font-family: 'MyCustomFont';
      src: url('/fonts/MyCustomFont.ttf') format('truetype');
  }
  ```
- Use it: `font-family: 'MyCustomFont', sans-serif;`

